/*
 * jQuery UI Effects Drop 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/drop-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.drop=function(C,G){var D=A(this),I=["position","top","bottom","left","right","opacity","height","width"],H=A.effects.setMode(D,C.mode||"hide"),K=H==="show",J=C.direction||"left",E=(J==="up"||J==="down")?"top":"left",L=(J==="up"||J==="left")?"pos":"neg",F={opacity:K?1:0},B;
A.effects.save(D,I);
D.show();
A.effects.createWrapper(D);
B=C.distance||D[E==="top"?"outerHeight":"outerWidth"](true)/2;
if(K){D.css("opacity",0).css(E,L==="pos"?-B:B)
}F[E]=(K?(L==="pos"?"+=":"-="):(L==="pos"?"-=":"+="))+B;
D.animate(F,{queue:false,duration:C.duration,easing:C.easing,complete:function(){if(H==="hide"){D.hide()
}A.effects.restore(D,I);
A.effects.removeWrapper(D);
G()
}})
}
}));