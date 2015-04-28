/*
 * jQuery UI Accordion 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/accordion/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget"],A)
}else{A(jQuery)
}}(function(A){return A.widget("ui.accordion",{version:"1.11.2",options:{active:0,animate:{},collapsible:false,event:"click",header:"> li > :first-child,> :not(li):even",heightStyle:"auto",icons:{activeHeader:"ui-icon-triangle-1-s",header:"ui-icon-triangle-1-e"},activate:null,beforeActivate:null},hideProps:{borderTopWidth:"hide",borderBottomWidth:"hide",paddingTop:"hide",paddingBottom:"hide",height:"hide"},showProps:{borderTopWidth:"show",borderBottomWidth:"show",paddingTop:"show",paddingBottom:"show",height:"show"},_create:function(){var B=this.options;
this.prevShow=this.prevHide=A();
this.element.addClass("ui-accordion ui-widget ui-helper-reset").attr("role","tablist");
if(!B.collapsible&&(B.active===false||B.active==null)){B.active=0
}this._processPanels();
if(B.active<0){B.active+=this.headers.length
}this._refresh()
},_getCreateEventData:function(){return{header:this.active,panel:!this.active.length?A():this.active.next()}
},_createIcons:function(){var B=this.options.icons;
if(B){A("<span>").addClass("ui-accordion-header-icon ui-icon "+B.header).prependTo(this.headers);
this.active.children(".ui-accordion-header-icon").removeClass(B.header).addClass(B.activeHeader);
this.headers.addClass("ui-accordion-icons")
}},_destroyIcons:function(){this.headers.removeClass("ui-accordion-icons").children(".ui-accordion-header-icon").remove()
},_destroy:function(){var B;
this.element.removeClass("ui-accordion ui-widget ui-helper-reset").removeAttr("role");
this.headers.removeClass("ui-accordion-header ui-accordion-header-active ui-state-default ui-corner-all ui-state-active ui-state-disabled ui-corner-top").removeAttr("role").removeAttr("aria-expanded").removeAttr("aria-selected").removeAttr("aria-controls").removeAttr("tabIndex").removeUniqueId();
this._destroyIcons();
B=this.headers.next().removeClass("ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content ui-accordion-content-active ui-state-disabled").css("display","").removeAttr("role").removeAttr("aria-hidden").removeAttr("aria-labelledby").removeUniqueId();
if(this.options.heightStyle!=="content"){B.css("height","")
}},_setOption:function(B,C){if(B==="active"){this._activate(C);
return 
}if(B==="event"){if(this.options.event){this._off(this.headers,this.options.event)
}this._setupEvents(C)
}this._super(B,C);
if(B==="collapsible"&&!C&&this.options.active===false){this._activate(0)
}if(B==="icons"){this._destroyIcons();
if(C){this._createIcons()
}}if(B==="disabled"){this.element.toggleClass("ui-state-disabled",!!C).attr("aria-disabled",C);
this.headers.add(this.headers.next()).toggleClass("ui-state-disabled",!!C)
}},_keydown:function(E){if(E.altKey||E.ctrlKey){return 
}var F=A.ui.keyCode,D=this.headers.length,B=this.headers.index(E.target),C=false;
switch(E.keyCode){case F.RIGHT:case F.DOWN:C=this.headers[(B+1)%D];
break;
case F.LEFT:case F.UP:C=this.headers[(B-1+D)%D];
break;
case F.SPACE:case F.ENTER:this._eventHandler(E);
break;
case F.HOME:C=this.headers[0];
break;
case F.END:C=this.headers[D-1];
break
}if(C){A(E.target).attr("tabIndex",-1);
A(C).attr("tabIndex",0);
C.focus();
E.preventDefault()
}},_panelKeyDown:function(B){if(B.keyCode===A.ui.keyCode.UP&&B.ctrlKey){A(B.currentTarget).prev().focus()
}},refresh:function(){var B=this.options;
this._processPanels();
if((B.active===false&&B.collapsible===true)||!this.headers.length){B.active=false;
this.active=A()
}else{if(B.active===false){this._activate(0)
}else{if(this.active.length&&!A.contains(this.element[0],this.active[0])){if(this.headers.length===this.headers.find(".ui-state-disabled").length){B.active=false;
this.active=A()
}else{this._activate(Math.max(0,B.active-1))
}}else{B.active=this.headers.index(this.active)
}}}this._destroyIcons();
this._refresh()
},_processPanels:function(){var C=this.headers,B=this.panels;
this.headers=this.element.find(this.options.header).addClass("ui-accordion-header ui-state-default ui-corner-all");
this.panels=this.headers.next().addClass("ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom").filter(":not(.ui-accordion-content-active)").hide();
if(B){this._off(C.not(this.headers));
this._off(B.not(this.panels))
}},_refresh:function(){var E,C=this.options,B=C.heightStyle,D=this.element.parent();
this.active=this._findActive(C.active).addClass("ui-accordion-header-active ui-state-active ui-corner-top").removeClass("ui-corner-all");
this.active.next().addClass("ui-accordion-content-active").show();
this.headers.attr("role","tab").each(function(){var I=A(this),H=I.uniqueId().attr("id"),F=I.next(),G=F.uniqueId().attr("id");
I.attr("aria-controls",G);
F.attr("aria-labelledby",H)
}).next().attr("role","tabpanel");
this.headers.not(this.active).attr({"aria-selected":"false","aria-expanded":"false",tabIndex:-1}).next().attr({"aria-hidden":"true"}).hide();
if(!this.active.length){this.headers.eq(0).attr("tabIndex",0)
}else{this.active.attr({"aria-selected":"true","aria-expanded":"true",tabIndex:0}).next().attr({"aria-hidden":"false"})
}this._createIcons();
this._setupEvents(C.event);
if(B==="fill"){E=D.height();
this.element.siblings(":visible").each(function(){var G=A(this),F=G.css("position");
if(F==="absolute"||F==="fixed"){return 
}E-=G.outerHeight(true)
});
this.headers.each(function(){E-=A(this).outerHeight(true)
});
this.headers.next().each(function(){A(this).height(Math.max(0,E-A(this).innerHeight()+A(this).height()))
}).css("overflow","auto")
}else{if(B==="auto"){E=0;
this.headers.next().each(function(){E=Math.max(E,A(this).css("height","").height())
}).height(E)
}}},_activate:function(B){var C=this._findActive(B)[0];
if(C===this.active[0]){return 
}C=C||this.active[0];
this._eventHandler({target:C,currentTarget:C,preventDefault:A.noop})
},_findActive:function(B){return typeof B==="number"?this.headers.eq(B):A()
},_setupEvents:function(C){var B={keydown:"_keydown"};
if(C){A.each(C.split(" "),function(E,D){B[D]="_eventHandler"
})
}this._off(this.headers.add(this.headers.next()));
this._on(this.headers,B);
this._on(this.headers.next(),{keydown:"_panelKeyDown"});
this._hoverable(this.headers);
this._focusable(this.headers)
},_eventHandler:function(B){var J=this.options,E=this.active,F=A(B.currentTarget),H=F[0]===E[0],C=H&&J.collapsible,D=C?A():F.next(),G=E.next(),I={oldHeader:E,oldPanel:G,newHeader:C?A():F,newPanel:D};
B.preventDefault();
if((H&&!J.collapsible)||(this._trigger("beforeActivate",B,I)===false)){return 
}J.active=C?false:this.headers.index(F);
this.active=H?A():F;
this._toggle(I);
E.removeClass("ui-accordion-header-active ui-state-active");
if(J.icons){E.children(".ui-accordion-header-icon").removeClass(J.icons.activeHeader).addClass(J.icons.header)
}if(!H){F.removeClass("ui-corner-all").addClass("ui-accordion-header-active ui-state-active ui-corner-top");
if(J.icons){F.children(".ui-accordion-header-icon").removeClass(J.icons.header).addClass(J.icons.activeHeader)
}F.next().addClass("ui-accordion-content-active")
}},_toggle:function(D){var B=D.newPanel,C=this.prevShow.length?this.prevShow:D.oldPanel;
this.prevShow.add(this.prevHide).stop(true,true);
this.prevShow=B;
this.prevHide=C;
if(this.options.animate){this._animate(B,C,D)
}else{C.hide();
B.show();
this._toggleComplete(D)
}C.attr({"aria-hidden":"true"});
C.prev().attr("aria-selected","false");
if(B.length&&C.length){C.prev().attr({tabIndex:-1,"aria-expanded":"false"})
}else{if(B.length){this.headers.filter(function(){return A(this).attr("tabIndex")===0
}).attr("tabIndex",-1)
}}B.attr("aria-hidden","false").prev().attr({"aria-selected":"true",tabIndex:0,"aria-expanded":"true"})
},_animate:function(B,J,F){var I,H,E,G=this,K=0,L=B.length&&(!J.length||(B.index()<J.index())),D=this.options.animate||{},M=L&&D.down||D,C=function(){G._toggleComplete(F)
};
if(typeof M==="number"){E=M
}if(typeof M==="string"){H=M
}H=H||M.easing||D.easing;
E=E||M.duration||D.duration;
if(!J.length){return B.animate(this.showProps,E,H,C)
}if(!B.length){return J.animate(this.hideProps,E,H,C)
}I=B.show().outerHeight();
J.animate(this.hideProps,{duration:E,easing:H,step:function(N,O){O.now=Math.round(N)
}});
B.hide().animate(this.showProps,{duration:E,easing:H,complete:C,step:function(N,O){O.now=Math.round(N);
if(O.prop!=="height"){K+=O.now
}else{if(G.options.heightStyle!=="content"){O.now=Math.round(I-J.outerHeight()-K);
K=0
}}}})
},_toggleComplete:function(C){var B=C.oldPanel;
B.removeClass("ui-accordion-content-active").prev().removeClass("ui-corner-top").addClass("ui-corner-all");
if(B.length){B.parent()[0].className=B.parent()[0].className
}this._trigger("activate",null,C)
}})
}));