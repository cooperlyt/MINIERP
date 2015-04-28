/*
 * jQuery UI Effects Bounce 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/bounce-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.bounce=function(K,J){var B=A(this),C=["position","top","bottom","left","right","height","width"],I=A.effects.setMode(B,K.mode||"effect"),H=I==="hide",S=I==="show",T=K.direction||"up",D=K.distance,G=K.times||5,U=G*2+(S||H?1:0),R=K.duration/U,M=K.easing,E=(T==="up"||T==="down")?"top":"left",L=(T==="up"||T==="left"),Q,F,P,N=B.queue(),O=N.length;
if(S||H){C.push("opacity")
}A.effects.save(B,C);
B.show();
A.effects.createWrapper(B);
if(!D){D=B[E==="top"?"outerHeight":"outerWidth"]()/3
}if(S){P={opacity:1};
P[E]=0;
B.css("opacity",0).css(E,L?-D*2:D*2).animate(P,R,M)
}if(H){D=D/Math.pow(2,G-1)
}P={};
P[E]=0;
for(Q=0;
Q<G;
Q++){F={};
F[E]=(L?"-=":"+=")+D;
B.animate(F,R,M).animate(P,R,M);
D=H?D*2:D/2
}if(H){F={opacity:0};
F[E]=(L?"-=":"+=")+D;
B.animate(F,R,M)
}B.queue(function(){if(H){B.hide()
}A.effects.restore(B,C);
A.effects.removeWrapper(B);
J()
});
if(O>1){N.splice.apply(N,[1,0].concat(N.splice(O,U+1)))
}B.dequeue()
}
}));