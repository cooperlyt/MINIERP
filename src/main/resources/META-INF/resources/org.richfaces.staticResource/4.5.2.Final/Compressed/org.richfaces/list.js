(function(F,D){D.ui=D.ui||{};
D.ui.List=function(J,H){G.constructor.call(this,J);
this.namespace=this.namespace||"."+D.Event.createNamespace(this.name,this.id);
this.attachToDom();
var I=F.extend({},A,H);
this.list=F(document.getElementById(J));
this.selectListener=I.selectListener;
this.selectItemCss=I.selectItemCss;
this.selectItemCssMarker=I.selectItemCss.split(" ",1)[0];
this.scrollContainer=F(I.scrollContainer);
this.itemCss=I.itemCss.split(" ",1)[0];
this.listCss=I.listCss;
this.clickRequiredToSelect=I.clickRequiredToSelect;
this.index=-1;
this.disabled=I.disabled;
this.focusKeeper=F(document.getElementById(J+"FocusKeeper"));
this.focusKeeper.focused=false;
this.isMouseDown=false;
this.list.bind("mousedown",F.proxy(this.__onMouseDown,this)).bind("mouseup",F.proxy(this.__onMouseUp,this));
B.call(this);
if(I.focusKeeperEnabled){C.call(this)
}this.__updateItemsList();
if(I.clientSelectItems!==null){this.__storeClientSelectItems(I.clientSelectItems)
}};
D.BaseComponent.extend(D.ui.List);
var G=D.ui.List.$super;
var A={clickRequiredToSelect:false,disabled:false,selectListener:false,clientSelectItems:null,focusKeeperEnabled:true};
var B=function(){var H={};
H["click"+this.namespace]=F.proxy(this.onClick,this);
H["dblclick"+this.namespace]=F.proxy(this.onDblclick,this);
this.list.on("mouseover"+this.namespace,"."+this.itemCss,F.proxy(E,this));
D.Event.bind(this.list,H,this)
};
var C=function(){var H={};
H["keydown"+this.namespace]=F.proxy(this.__keydownHandler,this);
H["blur"+this.namespace]=F.proxy(this.__blurHandler,this);
H["focus"+this.namespace]=F.proxy(this.__focusHandler,this);
D.Event.bind(this.focusKeeper,H,this)
};
var E=function(I){var H=F(I.target);
if(H&&!this.clickRequiredToSelect&&!this.disabled){this.__select(H)
}};
F.extend(D.ui.List.prototype,(function(){return{name:"list",processItem:function(H){if(this.selectListener.processItem&&typeof this.selectListener.processItem=="function"){this.selectListener.processItem(H)
}},isSelected:function(H){return H.hasClass(this.selectItemCssMarker)
},selectItem:function(H){if(this.selectListener.selectItem&&typeof this.selectListener.selectItem=="function"){this.selectListener.selectItem(H)
}else{H.addClass(this.selectItemCss);
D.Event.fire(this,"selectItem",H)
}this.__scrollToSelectedItem(this)
},unselectItem:function(H){if(this.selectListener.unselectItem&&typeof this.selectListener.unselectItem=="function"){this.selectListener.unselectItem(H)
}else{H.removeClass(this.selectItemCss);
D.Event.fire(this,"unselectItem",H)
}},__focusHandler:function(H){if(!this.focusKeeper.focused){this.focusKeeper.focused=true;
D.Event.fire(this,"listfocus"+this.namespace,H)
}},__blurHandler:function(I){if(!this.isMouseDown){var H=this;
this.timeoutId=window.setTimeout(function(){H.focusKeeper.focused=false;
H.invokeEvent.call(H,"blur",document.getElementById(H.id),I);
D.Event.fire(H,"listblur"+H.namespace,I)
},200)
}else{this.isMouseDown=false
}},__onMouseDown:function(H){this.isMouseDown=true
},__onMouseUp:function(H){this.isMouseDown=false
},__keydownHandler:function(I){if(I.isDefaultPrevented()){return 
}if(I.metaKey||I.ctrlKey){return 
}var H;
if(I.keyCode){H=I.keyCode
}else{if(I.which){H=I.which
}}switch(H){case D.KEYS.DOWN:I.preventDefault();
this.__selectNext();
break;
case D.KEYS.UP:I.preventDefault();
this.__selectPrev();
break;
case D.KEYS.HOME:I.preventDefault();
this.__selectByIndex(0);
break;
case D.KEYS.END:I.preventDefault();
this.__selectByIndex(this.items.length-1);
break;
default:break
}},onClick:function(I){this.setFocus();
var H=this.__getItem(I);
if(!H){return 
}this.processItem(H);
var J=I.metaKey||I.ctrlKey;
if(!this.disabled){this.__select(H,J&&this.clickRequiredToSelect)
}},onDblclick:function(I){this.setFocus();
var H=this.__getItem(I);
if(!H){return 
}this.processItem(H);
if(!this.disabled){this.__select(H,false)
}},currentSelectItem:function(){if(this.items&&this.index!=-1){return F(this.items[this.index])
}},getSelectedItemIndex:function(){return this.index
},removeItems:function(H){F(H).detach();
this.__updateItemsList();
D.Event.fire(this,"removeitems",H)
},removeAllItems:function(){var H=this.__getItems();
this.removeItems(H);
return H
},addItems:function(H){var I=this.scrollContainer;
I.append(H);
this.__updateItemsList();
D.Event.fire(this,"additems",H)
},move:function(H,J){if(J===0){return 
}var I=this;
if(J>0){H=F(H.get().reverse())
}H.each(function(M){var L=I.items.index(this);
var K=L+J;
var N=I.items[K];
if(J<0){F(this).insertBefore(N)
}else{F(this).insertAfter(N)
}I.index=I.index+J;
I.__updateItemsList()
});
D.Event.fire(this,"moveitems",H)
},getItemByIndex:function(H){if(H>=0&&H<this.items.length){return this.items[H]
}},getClientSelectItemByIndex:function(H){if(H>=0&&H<this.items.length){return F(this.items[H]).data("clientSelectItem")
}},resetSelection:function(){var H=this.currentSelectItem();
if(H){this.unselectItem(F(H))
}this.index=-1
},isList:function(H){var I=H.parents("."+this.listCss).attr("id");
return(I&&(I==this.getId()))
},length:function(){return this.items.length
},__updateIndex:function(I){if(I===null){this.index=-1
}else{var H=this.items.index(I);
if(H<0){H=0
}else{if(H>=this.items.length){H=this.items.length-1
}}this.index=H
}},__updateItemsList:function(){return(this.items=this.list.find("."+this.itemCss))
},__storeClientSelectItems:function(H){var I=[];
F.each(H,function(J){I[this.id]=this
});
this.items.each(function(J){var K=F(this);
var M=K.attr("id");
var L=I[M];
K.data("clientSelectItem",L)
})
},__select:function(I,J){var H=this.items.index(I);
this.__selectByIndex(H,J)
},__selectByIndex:function(H,J){if(!this.__isSelectByIndexValid(H)){return 
}if(!this.clickRequiredToSelect&&this.index==H){return 
}var K=this.__unselectPrevious();
if(this.clickRequiredToSelect&&K==H){return 
}this.index=this.__sanitizeSelectedIndex(H);
var I=this.items.eq(this.index);
if(this.isSelected(I)){this.unselectItem(I)
}else{this.selectItem(I)
}},__isSelectByIndexValid:function(H){if(this.items.length==0){return false
}if(H==undefined){this.index=-1;
return false
}return true
},__sanitizeSelectedIndex:function(I){var H;
if(I<0){H=0
}else{if(I>=this.items.length){H=this.items.length-1
}else{H=I
}}return H
},__unselectPrevious:function(){var I=this.index;
if(I!=-1){var H=this.items.eq(I);
this.unselectItem(H);
this.index=-1
}return I
},__selectItemByValue:function(J){var I=null;
this.resetSelection();
var H=this;
this.__getItems().each(function(K){if(F(this).data("clientSelectItem").value==J){H.__selectByIndex(K);
I=F(this);
return false
}});
return I
},csvEncodeValues:function(){var H=new Array();
this.__getItems().each(function(I){H.push(F(this).data("clientSelectItem").value)
});
return H.join(",")
},__selectCurrent:function(){var H;
if(this.items&&this.index>=0){H=this.items.eq(this.index);
this.processItem(H)
}},__getAdjacentIndex:function(I){var H=this.index+I;
if(H<0){H=this.items.length-1
}else{if(H>=this.items.length){H=0
}}return H
},__selectPrev:function(){this.__selectByIndex(this.__getAdjacentIndex(-1))
},__selectNext:function(){this.__selectByIndex(this.__getAdjacentIndex(1))
},__getItem:function(H){return F(H.target).closest("."+this.itemCss,H.currentTarget).get(0)
},__getItems:function(){return this.items
},__setItems:function(H){this.items=H
},__scrollToSelectedItem:function(){if(this.scrollContainer){var H=this.scrollContainer[0].getBoundingClientRect(),J=this.items.get(this.index).getBoundingClientRect();
if(H.top<J.top&&J.bottom<H.bottom){return 
}var L=J.top,I=H.top,K=this.scrollContainer.scrollTop()+L-I;
this.scrollContainer.scrollTop(K)
}},setFocus:function(){this.focusKeeper.focus()
}}
})())
})(RichFaces.jQuery,window.RichFaces);