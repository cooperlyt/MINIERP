(function(C,B){B.ui=B.ui||{};
var A={mode:"server",attachToBody:false,showDelay:50,hideDelay:300,verticalOffset:0,horizontalOffset:0,showEvent:"mouseover",positionOffset:[0,0],cssRoot:"ddm",cssClasses:{}};
B.ui.MenuBase=function(G,F){D.constructor.call(this,G,F);
this.id=G;
this.namespace=this.namespace||"."+B.Event.createNamespace(this.name,this.id);
this.options={};
C.extend(this.options,A,F||{});
C.extend(this.options.cssClasses,E.call(this,this.options.cssRoot));
this.attachToDom(G);
this.element=B.getDomElement(this.id);
this.displayed=false;
this.options.positionOffset=[this.options.horizontalOffset,this.options.verticalOffset];
this.popup=new RichFaces.ui.Popup(this.id+"_list",{attachTo:this.id,direction:this.options.direction,jointPoint:this.options.jointPoint,positionType:this.options.positionType,positionOffset:this.options.positionOffset,attachToBody:this.options.attachToBody});
this.selectedGroup=null;
B.Event.bindById(this.id,"mouseenter",C.proxy(this.__overHandler,this),this);
B.Event.bindById(this.id,"mouseleave",C.proxy(this.__leaveHandler,this),this);
this.popupElement=B.getDomElement(this.popup.id);
this.popupElement.tabIndex=-1;
this.__updateItemsList();
B.Event.bind(this.items,"mouseenter",C.proxy(this.__itemMouseEnterHandler,this),this);
this.currentSelectedItemIndex=-1;
var H;
H={};
H["keydown"+this.namespace]=this.__keydownHandler;
B.Event.bind(this.popupElement,H,this)
};
var E=function(G){var F={itemCss:"rf-"+G+"-itm",selectItemCss:"rf-"+G+"-itm-sel",unselectItemCss:"rf-"+G+"-itm-unsel",disabledItemCss:"rf-"+G+"-itm-dis",labelCss:"rf-"+G+"-lbl",listCss:"rf-"+G+"-lst",listContainerCss:"rf-"+G+"-lst-bg"};
return F
};
B.BaseComponent.extend(B.ui.MenuBase);
var D=B.ui.MenuBase.$super;
C.extend(B.ui.MenuBase.prototype,(function(){return{name:"MenuBase",show:function(){this.__showPopup()
},hide:function(){this.__hidePopup()
},processItem:function(F){if(F&&F.attr("id")&&!this.__isDisabled(F)&&!this.__isGroup(F)){this.invokeEvent("itemclick",B.getDomElement(this.id),null);
this.hide()
}},activateItem:function(G){var F=C(RichFaces.getDomElement(G));
B.Event.fireById(F.attr("id"),"click")
},__showPopup:function(F){if(!this.__isShown()){this.invokeEvent("show",B.getDomElement(this.id),null);
this.popup.show(F);
this.displayed=true;
B.ui.MenuManager.setActiveSubMenu(B.component(this.element))
}this.popupElement.focus()
},__hidePopup:function(){window.clearTimeout(this.showTimeoutId);
this.showTimeoutId=null;
if(this.__isShown()){this.invokeEvent("hide",B.getDomElement(this.id),null);
this.__closeChildGroups();
this.popup.hide();
this.displayed=false;
this.__deselectCurrentItem();
this.currentSelectedItemIndex=-1;
var F=B.component(this.__getParentMenu());
if(this.id!=F.id){F.popupElement.focus();
B.ui.MenuManager.setActiveSubMenu(F)
}}},__closeChildGroups:function(){var F=0;
var G;
for(F in this.items){G=this.items.eq(F);
if(this.__isGroup(G)){B.component(G).hide()
}}},__getParentMenuFromItem:function(F){var G;
if(F){G=F.parents("div."+this.options.cssClasses.itemCss).has("div."+this.options.cssClasses.listContainerCss).eq(1)
}if(G&&G.length>0){return G
}else{G=F.parents("div."+this.options.cssClasses.labelCss);
if(G&&G.length>0){return G
}else{return null
}}},__getParentMenu:function(){var G=C(this.element).parents("div."+this.options.cssClasses.itemCss).has("div."+this.options.cssClasses.listContainerCss).eq(0);
if(G&&G.length>0){return G
}else{var F=this.items.eq(0);
return this.__getParentMenuFromItem(F)
}},__isGroup:function(F){return F.find("div."+this.options.cssClasses.listCss).length>0
},__isDisabled:function(F){return F.hasClass(this.options.cssClasses.disabledItemCss)
},__isShown:function(){return this.displayed
},__itemMouseEnterHandler:function(G){var F=this.__getItemFromEvent(G);
if(F){if(this.currentSelectedItemIndex!=this.items.index(F)){this.__deselectCurrentItem();
this.currentSelectedItemIndex=this.items.index(F)
}}},__selectItem:function(F){if(!B.component(F).isSelected){B.component(F).select()
}},__getItemFromEvent:function(F){return C(F.target).closest("."+this.options.cssClasses.itemCss,F.currentTarget).eq(0)
},__showHandler:function(F){if(!this.__isShown()){this.showTimeoutId=window.setTimeout(C.proxy(function(){this.show(F)
},this),this.options.showDelay);
return false
}},__leaveHandler:function(){this.hideTimeoutId=window.setTimeout(C.proxy(function(){this.hide()
},this),this.options.hideDelay)
},__overHandler:function(){window.clearTimeout(this.hideTimeoutId);
this.hideTimeoutId=null
},destroy:function(){this.detach(this.id);
B.Event.unbind(this.popupElement,"keydown"+this.namespace);
this.popup.destroy();
this.popup=null;
D.destroy.call(this)
}}
})())
})(RichFaces.jQuery,RichFaces);