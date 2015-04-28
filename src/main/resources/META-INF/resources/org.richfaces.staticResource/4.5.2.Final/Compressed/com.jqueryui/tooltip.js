/*
 * jQuery UI Tooltip 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/tooltip/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./core","./widget","./position"],A)
}else{A(jQuery)
}}(function(A){return A.widget("ui.tooltip",{version:"1.11.2",options:{content:function(){var B=A(this).attr("title")||"";
return A("<a>").text(B).html()
},hide:true,items:"[title]:not([disabled])",position:{my:"left top+15",at:"left bottom",collision:"flipfit flip"},show:true,tooltipClass:null,track:false,close:null,open:null},_addDescribedBy:function(C,D){var B=(C.attr("aria-describedby")||"").split(/\s+/);
B.push(D);
C.data("ui-tooltip-id",D).attr("aria-describedby",A.trim(B.join(" ")))
},_removeDescribedBy:function(D){var E=D.data("ui-tooltip-id"),C=(D.attr("aria-describedby")||"").split(/\s+/),B=A.inArray(E,C);
if(B!==-1){C.splice(B,1)
}D.removeData("ui-tooltip-id");
C=A.trim(C.join(" "));
if(C){D.attr("aria-describedby",C)
}else{D.removeAttr("aria-describedby")
}},_create:function(){this._on({mouseover:"open",focusin:"open"});
this.tooltips={};
this.parents={};
if(this.options.disabled){this._disable()
}this.liveRegion=A("<div>").attr({role:"log","aria-live":"assertive","aria-relevant":"additions"}).addClass("ui-helper-hidden-accessible").appendTo(this.document[0].body)
},_setOption:function(B,D){var C=this;
if(B==="disabled"){this[D?"_disable":"_enable"]();
this.options[B]=D;
return 
}this._super(B,D);
if(B==="content"){A.each(this.tooltips,function(F,E){C._updateContent(E.element)
})
}},_disable:function(){var B=this;
A.each(this.tooltips,function(E,D){var C=A.Event("blur");
C.target=C.currentTarget=D.element[0];
B.close(C,true)
});
this.element.find(this.options.items).addBack().each(function(){var C=A(this);
if(C.is("[title]")){C.data("ui-tooltip-title",C.attr("title")).removeAttr("title")
}})
},_enable:function(){this.element.find(this.options.items).addBack().each(function(){var B=A(this);
if(B.data("ui-tooltip-title")){B.attr("title",B.data("ui-tooltip-title"))
}})
},open:function(C){var B=this,D=A(C?C.target:this.element).closest(this.options.items);
if(!D.length||D.data("ui-tooltip-id")){return 
}if(D.attr("title")){D.data("ui-tooltip-title",D.attr("title"))
}D.data("ui-tooltip-open",true);
if(C&&C.type==="mouseover"){D.parents().each(function(){var F=A(this),E;
if(F.data("ui-tooltip-open")){E=A.Event("blur");
E.target=E.currentTarget=this;
B.close(E,true)
}if(F.attr("title")){F.uniqueId();
B.parents[this.id]={element:this,title:F.attr("title")};
F.attr("title","")
}})
}this._updateContent(D,C)
},_updateContent:function(G,F){var E,B=this.options.content,D=this,C=F?F.type:null;
if(typeof B==="string"){return this._open(F,G,B)
}E=B.call(G[0],function(H){if(!G.data("ui-tooltip-open")){return 
}D._delay(function(){if(F){F.type=C
}this._open(F,G,H)
})
});
if(E){this._open(F,G,E)
}},_open:function(C,F,G){var B,K,J,I,D,H=A.extend({},this.options.position);
if(!G){return 
}B=this._find(F);
if(B){B.tooltip.find(".ui-tooltip-content").html(G);
return 
}if(F.is("[title]")){if(C&&C.type==="mouseover"){F.attr("title","")
}else{F.removeAttr("title")
}}B=this._tooltip(F);
K=B.tooltip;
this._addDescribedBy(F,K.attr("id"));
K.find(".ui-tooltip-content").html(G);
this.liveRegion.children().hide();
if(G.clone){D=G.clone();
D.removeAttr("id").find("[id]").removeAttr("id")
}else{D=G
}A("<div>").html(D).appendTo(this.liveRegion);
function E(L){H.of=L;
if(K.is(":hidden")){return 
}K.position(H)
}if(this.options.track&&C&&/^mouse/.test(C.type)){this._on(this.document,{mousemove:E});
E(C)
}else{K.position(A.extend({of:F},this.options.position))
}K.hide();
this._show(K,this.options.show);
if(this.options.show&&this.options.show.delay){I=this.delayedShow=setInterval(function(){if(K.is(":visible")){E(H.of);
clearInterval(I)
}},A.fx.interval)
}this._trigger("open",C,{tooltip:K});
J={keyup:function(L){if(L.keyCode===A.ui.keyCode.ESCAPE){var M=A.Event(L);
M.currentTarget=F[0];
this.close(M,true)
}}};
if(F[0]!==this.element[0]){J.remove=function(){this._removeTooltip(K)
}
}if(!C||C.type==="mouseover"){J.mouseleave="close"
}if(!C||C.type==="focusin"){J.focusout="close"
}this._on(true,F,J)
},close:function(C){var E,B=this,F=A(C?C.currentTarget:this.element),D=this._find(F);
if(!D){return 
}E=D.tooltip;
if(D.closing){return 
}clearInterval(this.delayedShow);
if(F.data("ui-tooltip-title")&&!F.attr("title")){F.attr("title",F.data("ui-tooltip-title"))
}this._removeDescribedBy(F);
D.hiding=true;
E.stop(true);
this._hide(E,this.options.hide,function(){B._removeTooltip(A(this))
});
F.removeData("ui-tooltip-open");
this._off(F,"mouseleave focusout keyup");
if(F[0]!==this.element[0]){this._off(F,"remove")
}this._off(this.document,"mousemove");
if(C&&C.type==="mouseleave"){A.each(this.parents,function(H,G){A(G.element).attr("title",G.title);
delete B.parents[H]
})
}D.closing=true;
this._trigger("close",C,{tooltip:E});
if(!D.hiding){D.closing=false
}},_tooltip:function(B){var C=A("<div>").attr("role","tooltip").addClass("ui-tooltip ui-widget ui-corner-all ui-widget-content "+(this.options.tooltipClass||"")),D=C.uniqueId().attr("id");
A("<div>").addClass("ui-tooltip-content").appendTo(C);
C.appendTo(this.document[0].body);
return this.tooltips[D]={element:B,tooltip:C}
},_find:function(B){var C=B.data("ui-tooltip-id");
return C?this.tooltips[C]:null
},_removeTooltip:function(B){B.remove();
delete this.tooltips[B.attr("id")]
},_destroy:function(){var B=this;
A.each(this.tooltips,function(F,E){var D=A.Event("blur"),C=E.element;
D.target=D.currentTarget=C[0];
B.close(D,true);
A("#"+F).remove();
if(C.data("ui-tooltip-title")){if(!C.attr("title")){C.attr("title",C.data("ui-tooltip-title"))
}C.removeData("ui-tooltip-title")
}});
this.liveRegion.remove()
}})
}));