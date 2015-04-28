/*
 * jQuery UI Effects Clip 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/clip-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.clip=function(E,H){var F=A(this),L=["position","top","bottom","left","right","height","width"],K=A.effects.setMode(F,E.mode||"hide"),N=K==="show",M=E.direction||"vertical",J=M==="vertical",O=J?"height":"width",I=J?"top":"left",G={},C,D,B;
A.effects.save(F,L);
F.show();
C=A.effects.createWrapper(F).css({overflow:"hidden"});
D=(F[0].tagName==="IMG")?C:F;
B=D[O]();
if(N){D.css(O,0);
D.css(I,B/2)
}G[O]=N?B:0;
G[I]=N?0:B/2;
D.animate(G,{queue:false,duration:E.duration,easing:E.easing,complete:function(){if(!N){F.hide()
}A.effects.restore(F,L);
A.effects.removeWrapper(F);
H()
}})
}
}));