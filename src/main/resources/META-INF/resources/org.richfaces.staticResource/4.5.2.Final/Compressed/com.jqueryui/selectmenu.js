/*
 * jQuery UI Selectmenu 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/selectmenu
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget","./position","./menu"],A)
}else{A(jQuery)
}}(function(A){return A.widget("ui.selectmenu",{version:"1.11.2",defaultElement:"<select>",options:{appendTo:null,disabled:null,icons:{button:"ui-icon-triangle-1-s"},position:{my:"left top",at:"left bottom",collision:"none"},width:null,change:null,close:null,focus:null,open:null,select:null},_create:function(){var B=this.element.uniqueId().attr("id");
this.ids={element:B,button:B+"-button",menu:B+"-menu"};
this._drawButton();
this._drawMenu();
if(this.options.disabled){this.disable()
}},_drawButton:function(){var C=this,B=this.element.attr("tabindex");
this.label=A("label[for='"+this.ids.element+"']").attr("for",this.ids.button);
this._on(this.label,{click:function(D){this.button.focus();
D.preventDefault()
}});
this.element.hide();
this.button=A("<span>",{"class":"ui-selectmenu-button ui-widget ui-state-default ui-corner-all",tabindex:B||this.options.disabled?-1:0,id:this.ids.button,role:"combobox","aria-expanded":"false","aria-autocomplete":"list","aria-owns":this.ids.menu,"aria-haspopup":"true"}).insertAfter(this.element);
A("<span>",{"class":"ui-icon "+this.options.icons.button}).prependTo(this.button);
this.buttonText=A("<span>",{"class":"ui-selectmenu-text"}).appendTo(this.button);
this._setText(this.buttonText,this.element.find("option:selected").text());
this._resizeButton();
this._on(this.button,this._buttonEvents);
this.button.one("focusin",function(){if(!C.menuItems){C._refreshMenu()
}});
this._hoverable(this.button);
this._focusable(this.button)
},_drawMenu:function(){var B=this;
this.menu=A("<ul>",{"aria-hidden":"true","aria-labelledby":this.ids.button,id:this.ids.menu});
this.menuWrap=A("<div>",{"class":"ui-selectmenu-menu ui-front"}).append(this.menu).appendTo(this._appendTo());
this.menuInstance=this.menu.menu({role:"listbox",select:function(C,D){C.preventDefault();
B._setSelection();
B._select(D.item.data("ui-selectmenu-item"),C)
},focus:function(D,E){var C=E.item.data("ui-selectmenu-item");
if(B.focusIndex!=null&&C.index!==B.focusIndex){B._trigger("focus",D,{item:C});
if(!B.isOpen){B._select(C,D)
}}B.focusIndex=C.index;
B.button.attr("aria-activedescendant",B.menuItems.eq(C.index).attr("id"))
}}).menu("instance");
this.menu.addClass("ui-corner-bottom").removeClass("ui-corner-all");
this.menuInstance._off(this.menu,"mouseleave");
this.menuInstance._closeOnDocumentClick=function(){return false
};
this.menuInstance._isDivider=function(){return false
}
},refresh:function(){this._refreshMenu();
this._setText(this.buttonText,this._getSelectedItem().text());
if(!this.options.width){this._resizeButton()
}},_refreshMenu:function(){this.menu.empty();
var C,B=this.element.find("option");
if(!B.length){return 
}this._parseOptions(B);
this._renderMenu(this.menu,this.items);
this.menuInstance.refresh();
this.menuItems=this.menu.find("li").not(".ui-selectmenu-optgroup");
C=this._getSelectedItem();
this.menuInstance.focus(null,C);
this._setAria(C.data("ui-selectmenu-item"));
this._setOption("disabled",this.element.prop("disabled"))
},open:function(B){if(this.options.disabled){return 
}if(!this.menuItems){this._refreshMenu()
}else{this.menu.find(".ui-state-focus").removeClass("ui-state-focus");
this.menuInstance.focus(null,this._getSelectedItem())
}this.isOpen=true;
this._toggleAttr();
this._resizeMenu();
this._position();
this._on(this.document,this._documentClick);
this._trigger("open",B)
},_position:function(){this.menuWrap.position(A.extend({of:this.button},this.options.position))
},close:function(B){if(!this.isOpen){return 
}this.isOpen=false;
this._toggleAttr();
this.range=null;
this._off(this.document);
this._trigger("close",B)
},widget:function(){return this.button
},menuWidget:function(){return this.menu
},_renderMenu:function(D,C){var E=this,B="";
A.each(C,function(F,G){if(G.optgroup!==B){A("<li>",{"class":"ui-selectmenu-optgroup ui-menu-divider"+(G.element.parent("optgroup").prop("disabled")?" ui-state-disabled":""),text:G.optgroup}).appendTo(D);
B=G.optgroup
}E._renderItemData(D,G)
})
},_renderItemData:function(B,C){return this._renderItem(B,C).data("ui-selectmenu-item",C)
},_renderItem:function(C,D){var B=A("<li>");
if(D.disabled){B.addClass("ui-state-disabled")
}this._setText(B,D.label);
return B.appendTo(C)
},_setText:function(B,C){if(C){B.text(C)
}else{B.html("&#160;")
}},_move:function(F,E){var D,C,B=".ui-menu-item";
if(this.isOpen){D=this.menuItems.eq(this.focusIndex)
}else{D=this.menuItems.eq(this.element[0].selectedIndex);
B+=":not(.ui-state-disabled)"
}if(F==="first"||F==="last"){C=D[F==="first"?"prevAll":"nextAll"](B).eq(-1)
}else{C=D[F+"All"](B).eq(0)
}if(C.length){this.menuInstance.focus(E,C)
}},_getSelectedItem:function(){return this.menuItems.eq(this.element[0].selectedIndex)
},_toggle:function(B){this[this.isOpen?"close":"open"](B)
},_setSelection:function(){var B;
if(!this.range){return 
}if(window.getSelection){B=window.getSelection();
B.removeAllRanges();
B.addRange(this.range)
}else{this.range.select()
}this.button.focus()
},_documentClick:{mousedown:function(B){if(!this.isOpen){return 
}if(!A(B.target).closest(".ui-selectmenu-menu, #"+this.ids.button).length){this.close(B)
}}},_buttonEvents:{mousedown:function(){var B;
if(window.getSelection){B=window.getSelection();
if(B.rangeCount){this.range=B.getRangeAt(0)
}}else{this.range=document.selection.createRange()
}},click:function(B){this._setSelection();
this._toggle(B)
},keydown:function(C){var B=true;
switch(C.keyCode){case A.ui.keyCode.TAB:case A.ui.keyCode.ESCAPE:this.close(C);
B=false;
break;
case A.ui.keyCode.ENTER:if(this.isOpen){this._selectFocusedItem(C)
}break;
case A.ui.keyCode.UP:if(C.altKey){this._toggle(C)
}else{this._move("prev",C)
}break;
case A.ui.keyCode.DOWN:if(C.altKey){this._toggle(C)
}else{this._move("next",C)
}break;
case A.ui.keyCode.SPACE:if(this.isOpen){this._selectFocusedItem(C)
}else{this._toggle(C)
}break;
case A.ui.keyCode.LEFT:this._move("prev",C);
break;
case A.ui.keyCode.RIGHT:this._move("next",C);
break;
case A.ui.keyCode.HOME:case A.ui.keyCode.PAGE_UP:this._move("first",C);
break;
case A.ui.keyCode.END:case A.ui.keyCode.PAGE_DOWN:this._move("last",C);
break;
default:this.menu.trigger(C);
B=false
}if(B){C.preventDefault()
}}},_selectFocusedItem:function(C){var B=this.menuItems.eq(this.focusIndex);
if(!B.hasClass("ui-state-disabled")){this._select(B.data("ui-selectmenu-item"),C)
}},_select:function(C,B){var D=this.element[0].selectedIndex;
this.element[0].selectedIndex=C.index;
this._setText(this.buttonText,C.label);
this._setAria(C);
this._trigger("select",B,{item:C});
if(C.index!==D){this._trigger("change",B,{item:C})
}this.close(B)
},_setAria:function(B){var C=this.menuItems.eq(B.index).attr("id");
this.button.attr({"aria-labelledby":C,"aria-activedescendant":C});
this.menu.attr("aria-activedescendant",C)
},_setOption:function(B,C){if(B==="icons"){this.button.find("span.ui-icon").removeClass(this.options.icons.button).addClass(C.button)
}this._super(B,C);
if(B==="appendTo"){this.menuWrap.appendTo(this._appendTo())
}if(B==="disabled"){this.menuInstance.option("disabled",C);
this.button.toggleClass("ui-state-disabled",C).attr("aria-disabled",C);
this.element.prop("disabled",C);
if(C){this.button.attr("tabindex",-1);
this.close()
}else{this.button.attr("tabindex",0)
}}if(B==="width"){this._resizeButton()
}},_appendTo:function(){var B=this.options.appendTo;
if(B){B=B.jquery||B.nodeType?A(B):this.document.find(B).eq(0)
}if(!B||!B[0]){B=this.element.closest(".ui-front")
}if(!B.length){B=this.document[0].body
}return B
},_toggleAttr:function(){this.button.toggleClass("ui-corner-top",this.isOpen).toggleClass("ui-corner-all",!this.isOpen).attr("aria-expanded",this.isOpen);
this.menuWrap.toggleClass("ui-selectmenu-open",this.isOpen);
this.menu.attr("aria-hidden",!this.isOpen)
},_resizeButton:function(){var B=this.options.width;
if(!B){B=this.element.show().outerWidth();
this.element.hide()
}this.button.outerWidth(B)
},_resizeMenu:function(){this.menu.outerWidth(Math.max(this.button.outerWidth(),this.menu.width("").outerWidth()+1))
},_getCreateOptions:function(){return{disabled:this.element.prop("disabled")}
},_parseOptions:function(B){var C=[];
B.each(function(E,G){var F=A(G),D=F.parent("optgroup");
C.push({element:F,index:E,value:F.attr("value"),label:F.text(),optgroup:D.attr("label")||"",disabled:D.prop("disabled")||F.prop("disabled")})
});
this.items=C
},_destroy:function(){this.menuWrap.remove();
this.button.remove();
this.element.show();
this.element.removeUniqueId();
this.label.attr("for",this.ids.element)
}})
}));