/*
 * jQuery UI Tabs 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/tabs/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget"],A)
}else{A(jQuery)
}}(function(A){return A.widget("ui.tabs",{version:"1.11.2",delay:300,options:{active:null,collapsible:false,event:"click",heightStyle:"content",hide:null,show:null,activate:null,beforeActivate:null,beforeLoad:null,load:null},_isLocal:(function(){var B=/#.*$/;
return function(D){var F,E;
D=D.cloneNode(false);
F=D.href.replace(B,"");
E=location.href.replace(B,"");
try{F=decodeURIComponent(F)
}catch(C){}try{E=decodeURIComponent(E)
}catch(C){}return D.hash.length>1&&F===E
}
})(),_create:function(){var C=this,B=this.options;
this.running=false;
this.element.addClass("ui-tabs ui-widget ui-widget-content ui-corner-all").toggleClass("ui-tabs-collapsible",B.collapsible);
this._processTabs();
B.active=this._initialActive();
if(A.isArray(B.disabled)){B.disabled=A.unique(B.disabled.concat(A.map(this.tabs.filter(".ui-state-disabled"),function(D){return C.tabs.index(D)
}))).sort()
}if(this.options.active!==false&&this.anchors.length){this.active=this._findActive(B.active)
}else{this.active=A()
}this._refresh();
if(this.active.length){this.load(B.active)
}},_initialActive:function(){var C=this.options.active,B=this.options.collapsible,D=location.hash.substring(1);
if(C===null){if(D){this.tabs.each(function(E,F){if(A(F).attr("aria-controls")===D){C=E;
return false
}})
}if(C===null){C=this.tabs.index(this.tabs.filter(".ui-tabs-active"))
}if(C===null||C===-1){C=this.tabs.length?0:false
}}if(C!==false){C=this.tabs.index(this.tabs.eq(C));
if(C===-1){C=B?false:0
}}if(!B&&C===false&&this.anchors.length){C=0
}return C
},_getCreateEventData:function(){return{tab:this.active,panel:!this.active.length?A():this._getPanelForTab(this.active)}
},_tabKeydown:function(D){var C=A(this.document[0].activeElement).closest("li"),B=this.tabs.index(C),E=true;
if(this._handlePageNav(D)){return 
}switch(D.keyCode){case A.ui.keyCode.RIGHT:case A.ui.keyCode.DOWN:B++;
break;
case A.ui.keyCode.UP:case A.ui.keyCode.LEFT:E=false;
B--;
break;
case A.ui.keyCode.END:B=this.anchors.length-1;
break;
case A.ui.keyCode.HOME:B=0;
break;
case A.ui.keyCode.SPACE:D.preventDefault();
clearTimeout(this.activating);
this._activate(B);
return ;
case A.ui.keyCode.ENTER:D.preventDefault();
clearTimeout(this.activating);
this._activate(B===this.options.active?false:B);
return ;
default:return 
}D.preventDefault();
clearTimeout(this.activating);
B=this._focusNextTab(B,E);
if(!D.ctrlKey){C.attr("aria-selected","false");
this.tabs.eq(B).attr("aria-selected","true");
this.activating=this._delay(function(){this.option("active",B)
},this.delay)
}},_panelKeydown:function(B){if(this._handlePageNav(B)){return 
}if(B.ctrlKey&&B.keyCode===A.ui.keyCode.UP){B.preventDefault();
this.active.focus()
}},_handlePageNav:function(B){if(B.altKey&&B.keyCode===A.ui.keyCode.PAGE_UP){this._activate(this._focusNextTab(this.options.active-1,false));
return true
}if(B.altKey&&B.keyCode===A.ui.keyCode.PAGE_DOWN){this._activate(this._focusNextTab(this.options.active+1,true));
return true
}},_findNextTab:function(C,D){var B=this.tabs.length-1;
function E(){if(C>B){C=0
}if(C<0){C=B
}return C
}while(A.inArray(E(),this.options.disabled)!==-1){C=D?C+1:C-1
}return C
},_focusNextTab:function(B,C){B=this._findNextTab(B,C);
this.tabs.eq(B).focus();
return B
},_setOption:function(B,C){if(B==="active"){this._activate(C);
return 
}if(B==="disabled"){this._setupDisabled(C);
return 
}this._super(B,C);
if(B==="collapsible"){this.element.toggleClass("ui-tabs-collapsible",C);
if(!C&&this.options.active===false){this._activate(0)
}}if(B==="event"){this._setupEvents(C)
}if(B==="heightStyle"){this._setupHeightStyle(C)
}},_sanitizeSelector:function(B){return B?B.replace(/[!"$%&'()*+,.\/:;<=>?@\[\]\^`{|}~]/g,"\\$&"):""
},refresh:function(){var C=this.options,B=this.tablist.children(":has(a[href])");
C.disabled=A.map(B.filter(".ui-state-disabled"),function(D){return B.index(D)
});
this._processTabs();
if(C.active===false||!this.anchors.length){C.active=false;
this.active=A()
}else{if(this.active.length&&!A.contains(this.tablist[0],this.active[0])){if(this.tabs.length===C.disabled.length){C.active=false;
this.active=A()
}else{this._activate(this._findNextTab(Math.max(0,C.active-1),false))
}}else{C.active=this.tabs.index(this.active)
}}this._refresh()
},_refresh:function(){this._setupDisabled(this.options.disabled);
this._setupEvents(this.options.event);
this._setupHeightStyle(this.options.heightStyle);
this.tabs.not(this.active).attr({"aria-selected":"false","aria-expanded":"false",tabIndex:-1});
this.panels.not(this._getPanelForTab(this.active)).hide().attr({"aria-hidden":"true"});
if(!this.active.length){this.tabs.eq(0).attr("tabIndex",0)
}else{this.active.addClass("ui-tabs-active ui-state-active").attr({"aria-selected":"true","aria-expanded":"true",tabIndex:0});
this._getPanelForTab(this.active).show().attr({"aria-hidden":"false"})
}},_processTabs:function(){var D=this,E=this.tabs,C=this.anchors,B=this.panels;
this.tablist=this._getList().addClass("ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all").attr("role","tablist").delegate("> li","mousedown"+this.eventNamespace,function(F){if(A(this).is(".ui-state-disabled")){F.preventDefault()
}}).delegate(".ui-tabs-anchor","focus"+this.eventNamespace,function(){if(A(this).closest("li").is(".ui-state-disabled")){this.blur()
}});
this.tabs=this.tablist.find("> li:has(a[href])").addClass("ui-state-default ui-corner-top").attr({role:"tab",tabIndex:-1});
this.anchors=this.tabs.map(function(){return A("a",this)[0]
}).addClass("ui-tabs-anchor").attr({role:"presentation",tabIndex:-1});
this.panels=A();
this.anchors.each(function(K,I){var F,G,J,H=A(I).uniqueId().attr("id"),L=A(I).closest("li"),M=L.attr("aria-controls");
if(D._isLocal(I)){F=I.hash;
J=F.substring(1);
G=D.element.find(D._sanitizeSelector(F))
}else{J=L.attr("aria-controls")||A({}).uniqueId()[0].id;
F="#"+J;
G=D.element.find(F);
if(!G.length){G=D._createPanel(J);
G.insertAfter(D.panels[K-1]||D.tablist)
}G.attr("aria-live","polite")
}if(G.length){D.panels=D.panels.add(G)
}if(M){L.data("ui-tabs-aria-controls",M)
}L.attr({"aria-controls":J,"aria-labelledby":H});
G.attr("aria-labelledby",H)
});
this.panels.addClass("ui-tabs-panel ui-widget-content ui-corner-bottom").attr("role","tabpanel");
if(E){this._off(E.not(this.tabs));
this._off(C.not(this.anchors));
this._off(B.not(this.panels))
}},_getList:function(){return this.tablist||this.element.find("ol,ul").eq(0)
},_createPanel:function(B){return A("<div>").attr("id",B).addClass("ui-tabs-panel ui-widget-content ui-corner-bottom").data("ui-tabs-destroy",true)
},_setupDisabled:function(D){if(A.isArray(D)){if(!D.length){D=false
}else{if(D.length===this.anchors.length){D=true
}}}for(var C=0,B;
(B=this.tabs[C]);
C++){if(D===true||A.inArray(C,D)!==-1){A(B).addClass("ui-state-disabled").attr("aria-disabled","true")
}else{A(B).removeClass("ui-state-disabled").removeAttr("aria-disabled")
}}this.options.disabled=D
},_setupEvents:function(C){var B={};
if(C){A.each(C.split(" "),function(E,D){B[D]="_eventHandler"
})
}this._off(this.anchors.add(this.tabs).add(this.panels));
this._on(true,this.anchors,{click:function(D){D.preventDefault()
}});
this._on(this.anchors,B);
this._on(this.tabs,{keydown:"_tabKeydown"});
this._on(this.panels,{keydown:"_panelKeydown"});
this._focusable(this.tabs);
this._hoverable(this.tabs)
},_setupHeightStyle:function(B){var D,C=this.element.parent();
if(B==="fill"){D=C.height();
D-=this.element.outerHeight()-this.element.height();
this.element.siblings(":visible").each(function(){var F=A(this),E=F.css("position");
if(E==="absolute"||E==="fixed"){return 
}D-=F.outerHeight(true)
});
this.element.children().not(this.panels).each(function(){D-=A(this).outerHeight(true)
});
this.panels.each(function(){A(this).height(Math.max(0,D-A(this).innerHeight()+A(this).height()))
}).css("overflow","auto")
}else{if(B==="auto"){D=0;
this.panels.each(function(){D=Math.max(D,A(this).height("").height())
}).height(D)
}}},_eventHandler:function(B){var K=this.options,F=this.active,G=A(B.currentTarget),E=G.closest("li"),I=E[0]===F[0],C=I&&K.collapsible,D=C?A():this._getPanelForTab(E),H=!F.length?A():this._getPanelForTab(F),J={oldTab:F,oldPanel:H,newTab:C?A():E,newPanel:D};
B.preventDefault();
if(E.hasClass("ui-state-disabled")||E.hasClass("ui-tabs-loading")||this.running||(I&&!K.collapsible)||(this._trigger("beforeActivate",B,J)===false)){return 
}K.active=C?false:this.tabs.index(E);
this.active=I?A():E;
if(this.xhr){this.xhr.abort()
}if(!H.length&&!D.length){A.error("jQuery UI Tabs: Mismatching fragment identifier.")
}if(D.length){this.load(this.tabs.index(E),B)
}this._toggle(B,J)
},_toggle:function(H,G){var F=this,B=G.newPanel,E=G.oldPanel;
this.running=true;
function D(){F.running=false;
F._trigger("activate",H,G)
}function C(){G.newTab.closest("li").addClass("ui-tabs-active ui-state-active");
if(B.length&&F.options.show){F._show(B,F.options.show,D)
}else{B.show();
D()
}}if(E.length&&this.options.hide){this._hide(E,this.options.hide,function(){G.oldTab.closest("li").removeClass("ui-tabs-active ui-state-active");
C()
})
}else{G.oldTab.closest("li").removeClass("ui-tabs-active ui-state-active");
E.hide();
C()
}E.attr("aria-hidden","true");
G.oldTab.attr({"aria-selected":"false","aria-expanded":"false"});
if(B.length&&E.length){G.oldTab.attr("tabIndex",-1)
}else{if(B.length){this.tabs.filter(function(){return A(this).attr("tabIndex")===0
}).attr("tabIndex",-1)
}}B.attr("aria-hidden","false");
G.newTab.attr({"aria-selected":"true","aria-expanded":"true",tabIndex:0})
},_activate:function(C){var B,D=this._findActive(C);
if(D[0]===this.active[0]){return 
}if(!D.length){D=this.active
}B=D.find(".ui-tabs-anchor")[0];
this._eventHandler({target:B,currentTarget:B,preventDefault:A.noop})
},_findActive:function(B){return B===false?A():this.tabs.eq(B)
},_getIndex:function(B){if(typeof B==="string"){B=this.anchors.index(this.anchors.filter("[href$='"+B+"']"))
}return B
},_destroy:function(){if(this.xhr){this.xhr.abort()
}this.element.removeClass("ui-tabs ui-widget ui-widget-content ui-corner-all ui-tabs-collapsible");
this.tablist.removeClass("ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all").removeAttr("role");
this.anchors.removeClass("ui-tabs-anchor").removeAttr("role").removeAttr("tabIndex").removeUniqueId();
this.tablist.unbind(this.eventNamespace);
this.tabs.add(this.panels).each(function(){if(A.data(this,"ui-tabs-destroy")){A(this).remove()
}else{A(this).removeClass("ui-state-default ui-state-active ui-state-disabled ui-corner-top ui-corner-bottom ui-widget-content ui-tabs-active ui-tabs-panel").removeAttr("tabIndex").removeAttr("aria-live").removeAttr("aria-busy").removeAttr("aria-selected").removeAttr("aria-labelledby").removeAttr("aria-hidden").removeAttr("aria-expanded").removeAttr("role")
}});
this.tabs.each(function(){var B=A(this),C=B.data("ui-tabs-aria-controls");
if(C){B.attr("aria-controls",C).removeData("ui-tabs-aria-controls")
}else{B.removeAttr("aria-controls")
}});
this.panels.show();
if(this.options.heightStyle!=="content"){this.panels.css("height","")
}},enable:function(B){var C=this.options.disabled;
if(C===false){return 
}if(B===undefined){C=false
}else{B=this._getIndex(B);
if(A.isArray(C)){C=A.map(C,function(D){return D!==B?D:null
})
}else{C=A.map(this.tabs,function(D,E){return E!==B?E:null
})
}}this._setupDisabled(C)
},disable:function(B){var C=this.options.disabled;
if(C===true){return 
}if(B===undefined){C=true
}else{B=this._getIndex(B);
if(A.inArray(B,C)!==-1){return 
}if(A.isArray(C)){C=A.merge([B],C).sort()
}else{C=[B]
}}this._setupDisabled(C)
},load:function(D,H){D=this._getIndex(D);
var G=this,E=this.tabs.eq(D),C=E.find(".ui-tabs-anchor"),B=this._getPanelForTab(E),F={tab:E,panel:B};
if(this._isLocal(C[0])){return 
}this.xhr=A.ajax(this._ajaxSettings(C,H,F));
if(this.xhr&&this.xhr.statusText!=="canceled"){E.addClass("ui-tabs-loading");
B.attr("aria-busy","true");
this.xhr.success(function(I){setTimeout(function(){B.html(I);
G._trigger("load",H,F)
},1)
}).complete(function(J,I){setTimeout(function(){if(I==="abort"){G.panels.stop(false,true)
}E.removeClass("ui-tabs-loading");
B.removeAttr("aria-busy");
if(J===G.xhr){delete G.xhr
}},1)
})
}},_ajaxSettings:function(B,E,D){var C=this;
return{url:B.attr("href"),beforeSend:function(G,F){return C._trigger("beforeLoad",E,A.extend({jqXHR:G,ajaxSettings:F},D))
}}
},_getPanelForTab:function(B){var C=A(B).attr("aria-controls");
return this.element.find(this._sanitizeSelector("#"+C))
}})
}));