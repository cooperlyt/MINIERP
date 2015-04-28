(function(D,C){C.ui=C.ui||{};
C.ui.PickList=function(I,F){var H=D.extend({},A,F);
E.constructor.call(this,I,H);
this.namespace=this.namespace||"."+C.Event.createNamespace(this.name,this.id);
this.attachToDom();
H.scrollContainer=D(document.getElementById(I+"SourceItems"));
this.sourceList=new C.ui.ListMulti(I+"SourceList",H);
H.scrollContainer=D(document.getElementById(I+"TargetItems"));
this.selectItemCss=H.selectItemCss;
var G=I+"SelValue";
this.hiddenValues=D(document.getElementById(G));
H.hiddenId=G;
this.orderable=H.orderable;
if(this.orderable){this.orderingList=new C.ui.OrderingList(I+"Target",H);
this.targetList=this.orderingList.list
}else{this.targetList=new C.ui.ListMulti(I+"TargetList",H)
}this.pickList=D(document.getElementById(I));
this.addButton=D(".rf-pick-add",this.pickList);
this.addButton.bind("click",D.proxy(this.add,this));
this.addAllButton=D(".rf-pick-add-all",this.pickList);
this.addAllButton.bind("click",D.proxy(this.addAll,this));
this.removeButton=D(".rf-pick-rem",this.pickList);
this.removeButton.bind("click",D.proxy(this.remove,this));
this.removeAllButton=D(".rf-pick-rem-all",this.pickList);
this.removeAllButton.bind("click",D.proxy(this.removeAll,this));
this.disabled=H.disabled;
if(H.onadditems&&typeof H.onadditems=="function"){C.Event.bind(this.targetList,"additems",H.onadditems)
}C.Event.bind(this.targetList,"additems",D.proxy(this.toggleButtons,this));
this.focused=false;
this.keepingFocus=false;
B.call(this,H);
if(H.onremoveitems&&typeof H.onremoveitems=="function"){C.Event.bind(this.sourceList,"additems",H.onremoveitems)
}C.Event.bind(this.sourceList,"additems",D.proxy(this.toggleButtons,this));
C.Event.bind(this.sourceList,"selectItem",D.proxy(this.toggleButtons,this));
C.Event.bind(this.sourceList,"unselectItem",D.proxy(this.toggleButtons,this));
C.Event.bind(this.targetList,"selectItem",D.proxy(this.toggleButtons,this));
C.Event.bind(this.targetList,"unselectItem",D.proxy(this.toggleButtons,this));
if(H.switchByClick){C.Event.bind(this.sourceList,"click",D.proxy(this.add,this));
C.Event.bind(this.targetList,"click",D.proxy(this.remove,this))
}if(H.switchByDblClick){C.Event.bind(this.sourceList,"dblclick",D.proxy(this.add,this));
C.Event.bind(this.targetList,"dblclick",D.proxy(this.remove,this))
}if(F.onchange&&typeof F.onchange=="function"){C.Event.bind(this,"change"+this.namespace,F.onchange)
}D(document).ready(D.proxy(this.toggleButtons,this))
};
C.BaseComponent.extend(C.ui.PickList);
var E=C.ui.PickList.$super;
var A={defaultLabel:"",itemCss:"rf-pick-opt",selectItemCss:"rf-pick-sel",listCss:"rf-pick-lst-cord",clickRequiredToSelect:true,switchByClick:false,switchByDblClick:true,disabled:false};
var B=function(F){if(F.onsourcefocus&&typeof F.onsourcefocus=="function"){C.Event.bind(this.sourceList,"listfocus"+this.sourceList.namespace,F.onsourcefocus)
}if(F.onsourceblur&&typeof F.onsourceblur=="function"){C.Event.bind(this.sourceList,"listblur"+this.sourceList.namespace,F.onsourceblur)
}if(F.ontargetfocus&&typeof F.ontargetfocus=="function"){C.Event.bind(this.targetList,"listfocus"+this.targetList.namespace,F.ontargetfocus)
}if(F.ontargetblur&&typeof F.ontargetblur=="function"){C.Event.bind(this.targetList,"listblur"+this.targetList.namespace,F.ontargetblur)
}if(F.onfocus&&typeof F.onfocus=="function"){C.Event.bind(this,"listfocus"+this.namespace,F.onfocus)
}if(F.onblur&&typeof F.onblur=="function"){C.Event.bind(this,"listblur"+this.namespace,F.onblur)
}this.pickList.focusin(D.proxy(this.__focusHandler,this));
this.pickList.focusout(D.proxy(this.__blurHandler,this))
};
D.extend(C.ui.PickList.prototype,(function(){return{name:"pickList",defaultLabelClass:"rf-pick-dflt-lbl",getName:function(){return this.name
},getNamespace:function(){return this.namespace
},__focusHandler:function(F){if(!this.focused){this.focused=true;
C.Event.fire(this,"listfocus"+this.namespace,F);
this.originalValue=this.targetList.csvEncodeValues()
}},__blurHandler:function(F){if(this.focused){this.focused=false;
C.Event.fire(this,"listblur"+this.namespace,F)
}},getSourceList:function(){return this.sourceList
},getTargetList:function(){return this.targetList
},add:function(){this.targetList.setFocus();
var F=this.sourceList.removeSelectedItems();
this.targetList.addItems(F);
this.encodeHiddenValues()
},remove:function(){this.sourceList.setFocus();
var F=this.targetList.removeSelectedItems();
this.sourceList.addItems(F);
this.encodeHiddenValues()
},addAll:function(){this.targetList.setFocus();
var F=this.sourceList.removeAllItems();
this.targetList.addItems(F);
this.encodeHiddenValues()
},removeAll:function(){this.sourceList.setFocus();
var F=this.targetList.removeAllItems();
this.sourceList.addItems(F);
this.encodeHiddenValues()
},encodeHiddenValues:function(){var F=this.hiddenValues.val();
var G=this.targetList.csvEncodeValues();
if(F!==G){this.hiddenValues.val(G)
}C.Event.fire(this,"change"+this.namespace,{oldValues:F,newValues:G})
},toggleButtons:function(){this.__toggleButton(this.addButton,this.sourceList.__getItems().filter("."+this.selectItemCss).length>0);
this.__toggleButton(this.removeButton,this.targetList.__getItems().filter("."+this.selectItemCss).length>0);
this.__toggleButton(this.addAllButton,this.sourceList.__getItems().length>0);
this.__toggleButton(this.removeAllButton,this.targetList.__getItems().length>0);
if(this.orderable){this.orderingList.toggleButtons()
}},__toggleButton:function(G,F){if(this.disabled||!F){if(!G.hasClass("rf-pick-btn-dis")){G.addClass("rf-pick-btn-dis")
}if(!G.attr("disabled")){G.attr("disabled",true)
}}else{if(G.hasClass("rf-pick-btn-dis")){G.removeClass("rf-pick-btn-dis")
}if(G.attr("disabled")){G.attr("disabled",false)
}}}}
})())
})(RichFaces.jQuery,window.RichFaces);