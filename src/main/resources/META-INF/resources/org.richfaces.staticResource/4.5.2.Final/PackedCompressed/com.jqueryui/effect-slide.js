/*
 * jQuery UI Effects Slide 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/slide-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.slide=function(D,H){var E=A(this),J=["position","top","bottom","left","right","width","height"],I=A.effects.setMode(E,D.mode||"show"),L=I==="show",K=D.direction||"left",F=(K==="up"||K==="down")?"top":"left",C=(K==="up"||K==="left"),B,G={};
A.effects.save(E,J);
E.show();
B=D.distance||E[F==="top"?"outerHeight":"outerWidth"](true);
A.effects.createWrapper(E).css({overflow:"hidden"});
if(L){E.css(F,C?(isNaN(B)?"-"+B:-B):B)
}G[F]=(L?(C?"+=":"-="):(C?"-=":"+="))+B;
E.animate(G,{queue:false,duration:D.duration,easing:D.easing,complete:function(){if(I==="hide"){E.hide()
}A.effects.restore(E,J);
A.effects.removeWrapper(E);
H()
}})
}
}));