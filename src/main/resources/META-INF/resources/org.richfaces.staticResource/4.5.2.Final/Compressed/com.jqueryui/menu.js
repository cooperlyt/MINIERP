/*
 * jQuery UI Menu 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/menu/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget","./position"],A)
}else{A(jQuery)
}}(function(A){return A.widget("ui.menu",{version:"1.11.2",defaultElement:"<ul>",delay:300,options:{icons:{submenu:"ui-icon-carat-1-e"},items:"> *",menus:"ul",position:{my:"left-1 top",at:"right top"},role:"menu",blur:null,focus:null,select:null},_create:function(){this.activeMenu=this.element;
this.mouseHandled=false;
this.element.uniqueId().addClass("ui-menu ui-widget ui-widget-content").toggleClass("ui-menu-icons",!!this.element.find(".ui-icon").length).attr({role:this.options.role,tabIndex:0});
if(this.options.disabled){this.element.addClass("ui-state-disabled").attr("aria-disabled","true")
}this._on({"mousedown .ui-menu-item":function(B){B.preventDefault()
},"click .ui-menu-item":function(B){var C=A(B.target);
if(!this.mouseHandled&&C.not(".ui-state-disabled").length){this.select(B);
if(!B.isPropagationStopped()){this.mouseHandled=true
}if(C.has(".ui-menu").length){this.expand(B)
}else{if(!this.element.is(":focus")&&A(this.document[0].activeElement).closest(".ui-menu").length){this.element.trigger("focus",[true]);
if(this.active&&this.active.parents(".ui-menu").length===1){clearTimeout(this.timer)
}}}}},"mouseenter .ui-menu-item":function(B){if(this.previousFilter){return 
}var C=A(B.currentTarget);
C.siblings(".ui-state-active").removeClass("ui-state-active");
this.focus(B,C)
},mouseleave:"collapseAll","mouseleave .ui-menu":"collapseAll",focus:function(D,B){var C=this.active||this.element.find(this.options.items).eq(0);
if(!B){this.focus(D,C)
}},blur:function(B){this._delay(function(){if(!A.contains(this.element[0],this.document[0].activeElement)){this.collapseAll(B)
}})
},keydown:"_keydown"});
this.refresh();
this._on(this.document,{click:function(B){if(this._closeOnDocumentClick(B)){this.collapseAll(B)
}this.mouseHandled=false
}})
},_destroy:function(){this.element.removeAttr("aria-activedescendant").find(".ui-menu").addBack().removeClass("ui-menu ui-widget ui-widget-content ui-menu-icons ui-front").removeAttr("role").removeAttr("tabIndex").removeAttr("aria-labelledby").removeAttr("aria-expanded").removeAttr("aria-hidden").removeAttr("aria-disabled").removeUniqueId().show();
this.element.find(".ui-menu-item").removeClass("ui-menu-item").removeAttr("role").removeAttr("aria-disabled").removeUniqueId().removeClass("ui-state-hover").removeAttr("tabIndex").removeAttr("role").removeAttr("aria-haspopup").children().each(function(){var B=A(this);
if(B.data("ui-menu-submenu-carat")){B.remove()
}});
this.element.find(".ui-menu-divider").removeClass("ui-menu-divider ui-widget-content")
},_keydown:function(F){var C,E,G,D,B=true;
switch(F.keyCode){case A.ui.keyCode.PAGE_UP:this.previousPage(F);
break;
case A.ui.keyCode.PAGE_DOWN:this.nextPage(F);
break;
case A.ui.keyCode.HOME:this._move("first","first",F);
break;
case A.ui.keyCode.END:this._move("last","last",F);
break;
case A.ui.keyCode.UP:this.previous(F);
break;
case A.ui.keyCode.DOWN:this.next(F);
break;
case A.ui.keyCode.LEFT:this.collapse(F);
break;
case A.ui.keyCode.RIGHT:if(this.active&&!this.active.is(".ui-state-disabled")){this.expand(F)
}break;
case A.ui.keyCode.ENTER:case A.ui.keyCode.SPACE:this._activate(F);
break;
case A.ui.keyCode.ESCAPE:this.collapse(F);
break;
default:B=false;
E=this.previousFilter||"";
G=String.fromCharCode(F.keyCode);
D=false;
clearTimeout(this.filterTimer);
if(G===E){D=true
}else{G=E+G
}C=this._filterMenuItems(G);
C=D&&C.index(this.active.next())!==-1?this.active.nextAll(".ui-menu-item"):C;
if(!C.length){G=String.fromCharCode(F.keyCode);
C=this._filterMenuItems(G)
}if(C.length){this.focus(F,C);
this.previousFilter=G;
this.filterTimer=this._delay(function(){delete this.previousFilter
},1000)
}else{delete this.previousFilter
}}if(B){F.preventDefault()
}},_activate:function(B){if(!this.active.is(".ui-state-disabled")){if(this.active.is("[aria-haspopup='true']")){this.expand(B)
}else{this.select(B)
}}},refresh:function(){var F,C,E=this,D=this.options.icons.submenu,B=this.element.find(this.options.menus);
this.element.toggleClass("ui-menu-icons",!!this.element.find(".ui-icon").length);
B.filter(":not(.ui-menu)").addClass("ui-menu ui-widget ui-widget-content ui-front").hide().attr({role:this.options.role,"aria-hidden":"true","aria-expanded":"false"}).each(function(){var I=A(this),H=I.parent(),G=A("<span>").addClass("ui-menu-icon ui-icon "+D).data("ui-menu-submenu-carat",true);
H.attr("aria-haspopup","true").prepend(G);
I.attr("aria-labelledby",H.attr("id"))
});
F=B.add(this.element);
C=F.find(this.options.items);
C.not(".ui-menu-item").each(function(){var G=A(this);
if(E._isDivider(G)){G.addClass("ui-widget-content ui-menu-divider")
}});
C.not(".ui-menu-item, .ui-menu-divider").addClass("ui-menu-item").uniqueId().attr({tabIndex:-1,role:this._itemRole()});
C.filter(".ui-state-disabled").attr("aria-disabled","true");
if(this.active&&!A.contains(this.element[0],this.active[0])){this.blur()
}},_itemRole:function(){return{menu:"menuitem",listbox:"option"}[this.options.role]
},_setOption:function(B,C){if(B==="icons"){this.element.find(".ui-menu-icon").removeClass(this.options.icons.submenu).addClass(C.submenu)
}if(B==="disabled"){this.element.toggleClass("ui-state-disabled",!!C).attr("aria-disabled",C)
}this._super(B,C)
},focus:function(C,B){var E,D;
this.blur(C,C&&C.type==="focus");
this._scrollIntoView(B);
this.active=B.first();
D=this.active.addClass("ui-state-focus").removeClass("ui-state-active");
if(this.options.role){this.element.attr("aria-activedescendant",D.attr("id"))
}this.active.parent().closest(".ui-menu-item").addClass("ui-state-active");
if(C&&C.type==="keydown"){this._close()
}else{this.timer=this._delay(function(){this._close()
},this.delay)
}E=B.children(".ui-menu");
if(E.length&&C&&(/^mouse/.test(C.type))){this._startOpening(E)
}this.activeMenu=B.parent();
this._trigger("focus",C,{item:B})
},_scrollIntoView:function(E){var H,D,F,B,C,G;
if(this._hasScroll()){H=parseFloat(A.css(this.activeMenu[0],"borderTopWidth"))||0;
D=parseFloat(A.css(this.activeMenu[0],"paddingTop"))||0;
F=E.offset().top-this.activeMenu.offset().top-H-D;
B=this.activeMenu.scrollTop();
C=this.activeMenu.height();
G=E.outerHeight();
if(F<0){this.activeMenu.scrollTop(B+F)
}else{if(F+G>C){this.activeMenu.scrollTop(B+F-C+G)
}}}},blur:function(C,B){if(!B){clearTimeout(this.timer)
}if(!this.active){return 
}this.active.removeClass("ui-state-focus");
this.active=null;
this._trigger("blur",C,{item:this.active})
},_startOpening:function(B){clearTimeout(this.timer);
if(B.attr("aria-hidden")!=="true"){return 
}this.timer=this._delay(function(){this._close();
this._open(B)
},this.delay)
},_open:function(C){var B=A.extend({of:this.active},this.options.position);
clearTimeout(this.timer);
this.element.find(".ui-menu").not(C.parents(".ui-menu")).hide().attr("aria-hidden","true");
C.show().removeAttr("aria-hidden").attr("aria-expanded","true").position(B)
},collapseAll:function(C,B){clearTimeout(this.timer);
this.timer=this._delay(function(){var D=B?this.element:A(C&&C.target).closest(this.element.find(".ui-menu"));
if(!D.length){D=this.element
}this._close(D);
this.blur(C);
this.activeMenu=D
},this.delay)
},_close:function(B){if(!B){B=this.active?this.active.parent():this.element
}B.find(".ui-menu").hide().attr("aria-hidden","true").attr("aria-expanded","false").end().find(".ui-state-active").not(".ui-state-focus").removeClass("ui-state-active")
},_closeOnDocumentClick:function(B){return !A(B.target).closest(".ui-menu").length
},_isDivider:function(B){return !/[^\-\u2014\u2013\s]/.test(B.text())
},collapse:function(C){var B=this.active&&this.active.parent().closest(".ui-menu-item",this.element);
if(B&&B.length){this._close();
this.focus(C,B)
}},expand:function(C){var B=this.active&&this.active.children(".ui-menu ").find(this.options.items).first();
if(B&&B.length){this._open(B.parent());
this._delay(function(){this.focus(C,B)
})
}},next:function(B){this._move("next","first",B)
},previous:function(B){this._move("prev","last",B)
},isFirstItem:function(){return this.active&&!this.active.prevAll(".ui-menu-item").length
},isLastItem:function(){return this.active&&!this.active.nextAll(".ui-menu-item").length
},_move:function(E,C,D){var B;
if(this.active){if(E==="first"||E==="last"){B=this.active[E==="first"?"prevAll":"nextAll"](".ui-menu-item").eq(-1)
}else{B=this.active[E+"All"](".ui-menu-item").eq(0)
}}if(!B||!B.length||!this.active){B=this.activeMenu.find(this.options.items)[C]()
}this.focus(D,B)
},nextPage:function(D){var C,E,B;
if(!this.active){this.next(D);
return 
}if(this.isLastItem()){return 
}if(this._hasScroll()){E=this.active.offset().top;
B=this.element.height();
this.active.nextAll(".ui-menu-item").each(function(){C=A(this);
return C.offset().top-E-B<0
});
this.focus(D,C)
}else{this.focus(D,this.activeMenu.find(this.options.items)[!this.active?"first":"last"]())
}},previousPage:function(D){var C,E,B;
if(!this.active){this.next(D);
return 
}if(this.isFirstItem()){return 
}if(this._hasScroll()){E=this.active.offset().top;
B=this.element.height();
this.active.prevAll(".ui-menu-item").each(function(){C=A(this);
return C.offset().top-E+B>0
});
this.focus(D,C)
}else{this.focus(D,this.activeMenu.find(this.options.items).first())
}},_hasScroll:function(){return this.element.outerHeight()<this.element.prop("scrollHeight")
},select:function(B){this.active=this.active||A(B.target).closest(".ui-menu-item");
var C={item:this.active};
if(!this.active.has(".ui-menu").length){this.collapseAll(B,true)
}this._trigger("select",B,C)
},_filterMenuItems:function(D){var B=D.replace(/[\-\[\]{}()*+?.,\\\^$|#\s]/g,"\\$&"),C=new RegExp("^"+B,"i");
return this.activeMenu.find(this.options.items).filter(".ui-menu-item").filter(function(){return C.test(A.trim(A(this).text()))
})
}})
}));