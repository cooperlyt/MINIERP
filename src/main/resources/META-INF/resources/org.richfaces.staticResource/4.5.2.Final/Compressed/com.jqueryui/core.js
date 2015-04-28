/*
 * jQuery UI Core 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/category/ui-core/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery"],A)
}else{A(jQuery)
}}(function(A){A.ui=A.ui||{};
A.extend(A.ui,{version:"1.11.2",keyCode:{BACKSPACE:8,COMMA:188,DELETE:46,DOWN:40,END:35,ENTER:13,ESCAPE:27,HOME:36,LEFT:37,PAGE_DOWN:34,PAGE_UP:33,PERIOD:190,RIGHT:39,SPACE:32,TAB:9,UP:38}});
A.fn.extend({scrollParent:function(F){var E=this.css("position"),D=E==="absolute",G=F?/(auto|scroll|hidden)/:/(auto|scroll)/,H=this.parents().filter(function(){var I=A(this);
if(D&&I.css("position")==="static"){return false
}return G.test(I.css("overflow")+I.css("overflow-y")+I.css("overflow-x"))
}).eq(0);
return E==="fixed"||!H.length?A(this[0].ownerDocument||document):H
},uniqueId:(function(){var D=0;
return function(){return this.each(function(){if(!this.id){this.id="ui-id-"+(++D)
}})
}
})(),removeUniqueId:function(){return this.each(function(){if(/^ui-id-\d+$/.test(this.id)){A(this).removeAttr("id")
}})
}});
function C(F,D){var H,G,E,I=F.nodeName.toLowerCase();
if("area"===I){H=F.parentNode;
G=H.name;
if(!F.href||!G||H.nodeName.toLowerCase()!=="map"){return false
}E=A("img[usemap='#"+G+"']")[0];
return !!E&&B(E)
}return(/input|select|textarea|button|object/.test(I)?!F.disabled:"a"===I?F.href||D:D)&&B(F)
}function B(D){return A.expr.filters.visible(D)&&!A(D).parents().addBack().filter(function(){return A.css(this,"visibility")==="hidden"
}).length
}A.extend(A.expr[":"],{data:A.expr.createPseudo?A.expr.createPseudo(function(D){return function(E){return !!A.data(E,D)
}
}):function(F,E,D){return !!A.data(F,D[3])
},focusable:function(D){return C(D,!isNaN(A.attr(D,"tabindex")))
},tabbable:function(F){var D=A.attr(F,"tabindex"),E=isNaN(D);
return(E||D>=0)&&C(F,!E)
}});
if(!A("<a>").outerWidth(1).jquery){A.each(["Width","Height"],function(F,D){var E=D==="Width"?["Left","Right"]:["Top","Bottom"],G=D.toLowerCase(),I={innerWidth:A.fn.innerWidth,innerHeight:A.fn.innerHeight,outerWidth:A.fn.outerWidth,outerHeight:A.fn.outerHeight};
function H(L,K,J,M){A.each(E,function(){K-=parseFloat(A.css(L,"padding"+this))||0;
if(J){K-=parseFloat(A.css(L,"border"+this+"Width"))||0
}if(M){K-=parseFloat(A.css(L,"margin"+this))||0
}});
return K
}A.fn["inner"+D]=function(J){if(J===undefined){return I["inner"+D].call(this)
}return this.each(function(){A(this).css(G,H(this,J)+"px")
})
};
A.fn["outer"+D]=function(J,K){if(typeof J!=="number"){return I["outer"+D].call(this,J)
}return this.each(function(){A(this).css(G,H(this,J,true,K)+"px")
})
}
})
}if(!A.fn.addBack){A.fn.addBack=function(D){return this.add(D==null?this.prevObject:this.prevObject.filter(D))
}
}if(A("<a>").data("a-b","a").removeData("a-b").data("a-b")){A.fn.removeData=(function(D){return function(E){if(arguments.length){return D.call(this,A.camelCase(E))
}else{return D.call(this)
}}
})(A.fn.removeData)
}A.ui.ie=!!/msie [\w.]+/.exec(navigator.userAgent.toLowerCase());
A.fn.extend({focus:(function(D){return function(E,F){return typeof E==="number"?this.each(function(){var G=this;
setTimeout(function(){A(G).focus();
if(F){F.call(G)
}},E)
}):D.apply(this,arguments)
}
})(A.fn.focus),disableSelection:(function(){var D="onselectstart" in document.createElement("div")?"selectstart":"mousedown";
return function(){return this.bind(D+".ui-disableSelection",function(E){E.preventDefault()
})
}
})(),enableSelection:function(){return this.unbind(".ui-disableSelection")
},zIndex:function(G){if(G!==undefined){return this.css("zIndex",G)
}if(this.length){var E=A(this[0]),D,F;
while(E.length&&E[0]!==document){D=E.css("position");
if(D==="absolute"||D==="relative"||D==="fixed"){F=parseInt(E.css("zIndex"),10);
if(!isNaN(F)&&F!==0){return F
}}E=E.parent()
}}return 0
}});
A.ui.plugin={add:function(E,F,H){var D,G=A.ui[E].prototype;
for(D in H){G.plugins[D]=G.plugins[D]||[];
G.plugins[D].push([F,H[D]])
}},call:function(D,G,F,E){var H,I=D.plugins[G];
if(!I){return 
}if(!E&&(!D.element[0].parentNode||D.element[0].parentNode.nodeType===11)){return 
}for(H=0;
H<I.length;
H++){if(D.options[I[H][0]]){I[H][1].apply(D.element,F)
}}}}
}));