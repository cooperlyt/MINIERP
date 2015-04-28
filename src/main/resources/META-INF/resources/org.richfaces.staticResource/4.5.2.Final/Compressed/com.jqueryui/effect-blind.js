/*
 * jQuery UI Effects Blind 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/blind-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.blind=function(D,J){var E=A(this),N=/up|down|vertical/,M=/up|left|vertical|horizontal/,O=["position","top","bottom","left","right","height","width"],K=A.effects.setMode(E,D.mode||"hide"),P=D.direction||"up",G=N.test(P),F=G?"height":"width",L=G?"top":"left",R=M.test(P),I={},Q=K==="show",C,B,H;
if(E.parent().is(".ui-effects-wrapper")){A.effects.save(E.parent(),O)
}else{A.effects.save(E,O)
}E.show();
C=A.effects.createWrapper(E).css({overflow:"hidden"});
B=C[F]();
H=parseFloat(C.css(L))||0;
I[F]=Q?B:0;
if(!R){E.css(G?"bottom":"right",0).css(G?"top":"left","auto").css({position:"absolute"});
I[L]=Q?H:B+H
}if(Q){C.css(F,0);
if(!R){C.css(L,H+B)
}}C.animate(I,{duration:D.duration,easing:D.easing,queue:false,complete:function(){if(K==="hide"){E.hide()
}A.effects.restore(E,O);
A.effects.removeWrapper(E);
J()
}})
}
}));