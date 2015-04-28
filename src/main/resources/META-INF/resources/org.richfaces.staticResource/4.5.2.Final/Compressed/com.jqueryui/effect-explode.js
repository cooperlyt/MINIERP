/*
 * jQuery UI Effects Explode 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/explode-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.explode=function(O,N){var H=O.pieces?Math.round(Math.sqrt(O.pieces)):3,C=H,B=A(this),J=A.effects.setMode(B,O.mode||"hide"),S=J==="show",F=B.show().css("visibility","hidden").offset(),P=Math.ceil(B.outerWidth()/C),M=Math.ceil(B.outerHeight()/H),G=[],R,Q,D,L,K,I;
function T(){G.push(this);
if(G.length===H*C){E()
}}for(R=0;
R<H;
R++){L=F.top+R*M;
I=R-(H-1)/2;
for(Q=0;
Q<C;
Q++){D=F.left+Q*P;
K=Q-(C-1)/2;
B.clone().appendTo("body").wrap("<div></div>").css({position:"absolute",visibility:"visible",left:-Q*P,top:-R*M}).parent().addClass("ui-effects-explode").css({position:"absolute",overflow:"hidden",width:P,height:M,left:D+(S?K*P:0),top:L+(S?I*M:0),opacity:S?0:1}).animate({left:D+(S?0:K*P),top:L+(S?0:I*M),opacity:S?1:0},O.duration||500,O.easing,T)
}}function E(){B.css({visibility:"visible"});
A(G).remove();
if(!S){B.hide()
}N()
}}
}));