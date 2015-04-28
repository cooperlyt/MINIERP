/*
 * jQuery UI Effects Shake 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/shake-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.shake=function(J,I){var B=A(this),C=["position","top","bottom","left","right","height","width"],H=A.effects.setMode(B,J.mode||"effect"),R=J.direction||"left",D=J.distance||20,G=J.times||3,S=G*2+1,N=Math.round(J.duration/S),F=(R==="up"||R==="down")?"top":"left",E=(R==="up"||R==="left"),Q={},P={},O={},M,K=B.queue(),L=K.length;
A.effects.save(B,C);
B.show();
A.effects.createWrapper(B);
Q[F]=(E?"-=":"+=")+D;
P[F]=(E?"+=":"-=")+D*2;
O[F]=(E?"-=":"+=")+D*2;
B.animate(Q,N,J.easing);
for(M=1;
M<G;
M++){B.animate(P,N,J.easing).animate(O,N,J.easing)
}B.animate(P,N,J.easing).animate(Q,N/2,J.easing).queue(function(){if(H==="hide"){B.hide()
}A.effects.restore(B,C);
A.effects.removeWrapper(B);
I()
});
if(L>1){K.splice.apply(K,[1,0].concat(K.splice(L,S+1)))
}B.dequeue()
}
}));