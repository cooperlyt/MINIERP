(function(C,B){B.ui=B.ui||{};
var A={mode:"server",cssRoot:"ddm",cssClasses:{}};
B.ui.MenuItem=function(G,F){this.options={};
C.extend(this.options,A,F||{});
D.constructor.call(this,G);
C.extend(this.options.cssClasses,E.call(this,this.options.cssRoot));
this.attachToDom(G);
this.element=C(B.getDomElement(G));
B.Event.bindById(this.id,"click",this.__clickHandler,this);
B.Event.bindById(this.id,"mouseenter",this.select,this);
B.Event.bindById(this.id,"mouseleave",this.unselect,this);
this.selected=false
};
var E=function(G){var F={itemCss:"rf-"+G+"-itm",selectItemCss:"rf-"+G+"-itm-sel",unselectItemCss:"rf-"+G+"-itm-unsel",labelCss:"rf-"+G+"-lbl"};
return F
};
B.BaseComponent.extend(B.ui.MenuItem);
var D=B.ui.MenuItem.$super;
C.extend(B.ui.MenuItem.prototype,(function(){return{name:"MenuItem",select:function(){this.element.removeClass(this.options.cssClasses.unselectItemCss);
this.element.addClass(this.options.cssClasses.selectItemCss);
this.selected=true
},unselect:function(){this.element.removeClass(this.options.cssClasses.selectItemCss);
this.element.addClass(this.options.cssClasses.unselectItemCss);
this.selected=false
},activate:function(){this.invokeEvent("click",B.getDomElement(this.id))
},isSelected:function(){return this.selected
},__clickHandler:function(I){if(C(I.target).is(":input:not(:button):not(:reset):not(:submit)")){return 
}var F=this.__getParentMenu();
if(F){F.processItem(this.element)
}var H=B.getDomElement(this.id);
var K=this.options.params;
var G=this.__getParentForm(H);
var J={};
J[H.id]=H.id;
C.extend(J,K||{});
I.form=G;
I.itemId=J;
this.options.onClickHandler.call(this,I)
},__getParentForm:function(F){return C(C(F).parents("form").get(0))
},__getParentMenu:function(){var F=this.element.parents("div."+this.options.cssClasses.labelCss);
if(F&&F.length>0){return B.component(F)
}else{return null
}}}
})())
})(RichFaces.jQuery,RichFaces);