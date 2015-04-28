(function(D,C){C.ui=C.ui||{};
C.ui.Select=function(L,H){this.id=L;
this.element=this.attachToDom();
var K=D.extend({},B,H);
K.attachTo=L;
K.scrollContainer=D(document.getElementById(L+"Items")).parent()[0];
K.focusKeeperEnabled=false;
E.constructor.call(this,L,K);
this.options=K;
this.defaultLabel=K.defaultLabel;
var J=this.__getValue();
this.initialValue=(J!=this.defaultLabel)?J:"";
this.selValueInput=D(document.getElementById(L+"selValue"));
this.container=this.selValueInput.parent();
this.clientSelectItems=K.clientSelectItems;
this.filterFunction=K.filterFunction;
if(K.showControl&&!K.disabled){this.container.bind("mousedown",D.proxy(this.__onBtnMouseDown,this)).bind("mouseup",D.proxy(this.__onMouseUp,this))
}this.isFirstAjax=true;
this.previousValue=this.__getValue();
this.selectFirst=K.selectFirst;
this.popupList=new C.ui.PopupList((L+"List"),this,K);
this.list=this.popupList.__getList();
this.listElem=D(document.getElementById(L+"List"));
this.listElem.bind("mousedown",D.proxy(this.__onListMouseDown,this));
this.listElem.bind("mouseup",D.proxy(this.__onMouseUp,this));
var I={};
I["listshow"+this.namespace]=D.proxy(this.__listshowHandler,this);
I["listhide"+this.namespace]=D.proxy(this.__listhideHandler,this);
I["change"+this.namespace]=D.proxy(this.__onInputChangeHandler,this);
C.Event.bind(this.element,I,this);
this.originalItems=this.list.__getItems();
this.enableManualInput=K.enableManualInput||K.isAutocomplete;
if(this.enableManualInput){F.call(this,"",this.clientSelectItems)
}this.changeDelay=K.changeDelay
};
C.ui.InputBase.extend(C.ui.Select);
var E=C.ui.Select.$super;
var B={defaultLabel:"",selectFirst:true,showControl:true,enableManualInput:false,itemCss:"rf-sel-opt",selectItemCss:"rf-sel-sel",listCss:"rf-sel-lst-cord",changeDelay:8,disabled:false,filterFunction:undefined,isAutocomplete:false,ajaxMode:true,lazyClientMode:false,isCachedAjax:true};
var G=/^[\n\s]*(.*)[\n\s]*$/;
var F=function(I,H){if(!H){H=[]
}if(H.length||(!this.options.isAutocomplete&&!this.options.isCachedAjax)){this.clientSelectItems=H
}this.originalItems=this.list.__updateItemsList();
this.list.__storeClientSelectItems(H);
if(this.originalItems.length>0){this.cache=new C.utils.Cache((this.options.ajaxMode?I:""),this.originalItems,A,!this.options.ajaxMode)
}};
var A=function(H){var I=[];
H.each(function(){I.push(D(this).text().replace(G,"$1"))
});
return I
};
D.extend(C.ui.Select.prototype,(function(){return{name:"select",defaultLabelClass:"rf-sel-dflt-lbl",__listshowHandler:function(H){if(this.originalItems.length==0&&this.isFirstAjax){this.callAjax(H)
}},__listhideHandler:function(H){},__onInputChangeHandler:function(H){this.__setValue(this.input.val())
},__onBtnMouseDown:function(H){if(!this.popupList.isVisible()&&!this.options.isAutocomplete){this.__updateItems();
this.__showPopup()
}else{this.__hidePopup()
}this.isMouseDown=true
},__focusHandler:function(H){if(!this.focused){if(this.__getValue()==this.defaultLabel){this.__setValue("")
}this.focusValue=this.selValueInput.val();
this.focused=true;
this.invokeEvent.call(this,"focus",document.getElementById(this.id),H)
}},__keydownHandler:function(I){var H;
if(I.keyCode){H=I.keyCode
}else{if(I.which){H=I.which
}}var J=this.popupList.isVisible();
switch(H){case C.KEYS.DOWN:I.preventDefault();
I.stopPropagation();
if(!J){this.__updateItems();
this.__showPopup()
}else{this.list.__selectNext()
}break;
case C.KEYS.UP:I.preventDefault();
I.stopPropagation();
if(J){this.list.__selectPrev()
}break;
case C.KEYS.TAB:case C.KEYS.RETURN:if(H==C.KEYS.TAB&&!J){break
}I.preventDefault();
if(J){this.list.__selectCurrent()
}return false;
break;
case C.KEYS.ESC:I.preventDefault();
if(J){this.__hidePopup()
}break;
default:if(this.__selectItemByLabel(H)){break
}var K=this;
window.clearTimeout(this.changeTimerId);
this.changeTimerId=window.setTimeout(function(){K.__onChangeValue(I)
},this.changeDelay);
break
}},__onChangeValue:function(I){var H=this.__getValue();
if(H===this.previousValue){return 
}this.previousValue=H;
if(!this.options.isAutocomplete||(this.options.isCachedAjax||!this.options.ajaxMode)&&this.cache&&this.cache.isCached(H)){this.__updateItems();
if(this.isAutocomplete){this.originalItems=this.list.__getItems()
}if(this.list.__getItems().length!=0){this.container.removeClass("rf-sel-fld-err")
}else{this.container.addClass("rf-sel-fld-err")
}if(!this.popupList.isVisible()){this.__showPopup()
}}else{if(H.length>=this.options.minChars){if((this.options.ajaxMode||this.options.lazyClientMode)){this.callAjax(I)
}}else{if(this.options.ajaxMode){this.clearItems();
this.__hidePopup()
}}}},clearItems:function(){this.list.removeAllItems()
},callAjax:function(K){var M=this;
var H=K;
var J=function(N){F.call(M,M.__getValue(),N.componentData&&N.componentData[M.id]);
if(M.clientSelectItems&&M.clientSelectItems.length){M.__updateItems();
M.__showPopup()
}else{M.__hidePopup()
}};
var I=function(N){M.__hidePopup();
M.clearItems()
};
this.isFirstAjax=false;
var L={};
L[this.id+".ajax"]="1";
C.ajax(this.id,K,{parameters:L,error:I,complete:J})
},__blurHandler:function(I){if(!this.isMouseDown){var H=this;
this.timeoutId=window.setTimeout(function(){if(H.input!==null){H.onblur(I)
}},200)
}else{this.__setInputFocus();
this.isMouseDown=false
}},__onListMouseDown:function(H){this.isMouseDown=true
},__onMouseUp:function(H){this.isMouseDown=false;
this.__setInputFocus()
},__updateItems:function(){var H=this.__getValue();
H=(H!=this.defaultLabel)?H:"";
this.__updateItemsFromCache(H);
if(this.selectFirst&&this.enableManualInput&&!this.__isValueSelected(H)){this.list.__selectByIndex(0)
}},__updateItemsFromCache:function(J){if(this.originalItems.length>0&&(this.enableManualInput||this.isAutocomplete)&&!this.__isValueSelected(J)){var I=this.cache.getItems(J,this.filterFunction);
var H=D(I);
this.list.__unselectPrevious();
this.list.__setItems(H);
D(document.getElementById(this.id+"Items")).children().detach();
D(document.getElementById(this.id+"Items")).append(H)
}},__getClientItemFromCache:function(K){var J;
var I;
if(this.enableManualInput){var H=this.cache.getItems(K,this.filterFunction);
if(H&&H.length>0){var L=D(H[0]);
D.each(this.clientSelectItems,function(){if(this.id==L.attr("id")){I=this.label;
J=this.value;
return false
}})
}else{I=K;
J=""
}}if(I){return{label:I,value:J}
}},__getClientItem:function(J){var I;
var H=J;
D.each(this.clientSelectItems,function(){if(H==this.label){I=this.value
}});
if(H&&I){return{label:H,value:I}
}},__isValueSelected:function(H){var I=this.__getClientItemFromCache(H);
return I.label===H&&I.value==this.getValue()
},__selectItemByLabel:function(J){if(this.enableManualInput||J<48||(J>57&&J<65)||J>90){return false
}if(!this.popupList.isVisible()){this.__updateItems();
this.__showPopup()
}var H=new Array();
D.each(this.clientSelectItems,function(K){if(this.label[0].toUpperCase().charCodeAt(0)==J){H.push(K)
}});
if(H.length){var I=0;
if(this.lastKeyCode&&this.lastKeyCode==J){I=this.lastKeyCodeCount+1;
if(I>=H.length){I=0
}}this.lastKeyCode=J;
this.lastKeyCodeCount=I;
this.list.__selectByIndex(H[I])
}return false
},__showPopup:function(){if(this.originalItems.length>0){this.popupList.show();
if(!this.options.enableManualInput||this.__isValueSelected(this.getLabel())){if(this.originalItems.length>this.popupList.list.items.length){this.popupList.list.__unselectPrevious();
this.popupList.list.__setItems(this.originalItems);
D(document.getElementById(this.id+"Items")).children().detach();
D(document.getElementById(this.id+"Items")).append(this.originalItems)
}this.list.__selectItemByValue(this.getValue())
}}this.invokeEvent.call(this,"listshow",document.getElementById(this.id))
},__hidePopup:function(){this.popupList.hide();
this.invokeEvent.call(this,"listhide",document.getElementById(this.id))
},showPopup:function(){if(!this.popupList.isVisible()){this.__updateItems();
this.__showPopup()
}this.__setInputFocus();
if(!this.focused){if(this.__getValue()==this.defaultLabel){this.__setValue("")
}this.focusValue=this.selValueInput.val();
this.focused=true;
this.invokeEvent.call(this,"focus",document.getElementById(this.id))
}},hidePopup:function(){if(this.popupList.isVisible()){this.__hidePopup();
var H=this.__getValue();
if(!H||H==""){this.__setValue(this.defaultLabel);
this.selValueInput.val("")
}this.focused=false;
this.invokeEvent.call(this,"blur",document.getElementById(this.id));
if(this.focusValue!=this.selValueInput.val()){this.invokeEvent.call(this,"change",document.getElementById(this.id))
}}},processItem:function(J){var I=D(J).attr("id");
var H,K;
D.each(this.clientSelectItems,function(){if(this.id==I){H=this.label;
K=this.value;
return false
}});
this.__setValue(H);
this.selValueInput.val(K);
this.__hidePopup();
this.__setInputFocus();
this.invokeEvent.call(this,"selectitem",document.getElementById(this.id))
},__save:function(){var J="";
var H="";
var I=this.__getValue();
var K;
if(I&&I!=""){if(this.enableManualInput){K=this.__getClientItemFromCache(I)
}else{K=this.__getClientItem(I)
}if(K){H=K.label;
J=K.value
}}this.__setValue(H);
this.selValueInput.val(J)
},onblur:function(I){this.__hidePopup();
var H=this.__getValue();
if(!H||H==""){this.__setValue(this.defaultLabel);
this.selValueInput.val("")
}this.focused=false;
this.invokeEvent.call(this,"blur",document.getElementById(this.id),I);
if(this.focusValue!=this.selValueInput.val()){this.invokeEvent.call(this,"change",document.getElementById(this.id),I)
}},getValue:function(){return this.selValueInput.val()
},setValue:function(J){if(J==null||J==""){this.__setValue("");
this.__save();
this.__updateItems();
return 
}var I;
for(var H=0;
H<this.clientSelectItems.length;
H++){I=this.clientSelectItems[H];
if(I.value==J){this.__setValue(I.label);
this.__save();
this.list.__selectByIndex(H);
return 
}}},getLabel:function(){return this.__getValue()
},destroy:function(){this.popupList.destroy();
this.popupList=null;
E.destroy.call(this)
}}
})());
C.csv=C.csv||{};
C.csv.validateSelectLabelValue=function(H,N,M,L){var J=D(document.getElementById(N+"selValue")).val();
var I=D(document.getElementById(N+"Input")).val();
var K=RichFaces.component(N).defaultLabel;
if(!J&&I&&(I!=K)){throw C.csv.getMessage(null,"UISELECTONE_INVALID",[N,""])
}}
})(RichFaces.jQuery,window.RichFaces);