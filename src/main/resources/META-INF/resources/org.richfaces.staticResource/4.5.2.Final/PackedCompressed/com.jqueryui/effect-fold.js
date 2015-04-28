/*
 * jQuery UI Effects Fold 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/fold-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.fold=function(D,H){var E=A(this),M=["position","top","bottom","left","right","height","width"],J=A.effects.setMode(E,D.mode||"hide"),P=J==="show",K=J==="hide",R=D.size||15,L=/([0-9]+)%/.exec(R),Q=!!D.horizFirst,I=P!==Q,F=I?["width","height"]:["height","width"],G=D.duration/2,C,B,O={},N={};
A.effects.save(E,M);
E.show();
C=A.effects.createWrapper(E).css({overflow:"hidden"});
B=I?[C.width(),C.height()]:[C.height(),C.width()];
if(L){R=parseInt(L[1],10)/100*B[K?0:1]
}if(P){C.css(Q?{height:0,width:R}:{height:R,width:0})
}O[F[0]]=P?B[0]:R;
N[F[1]]=P?B[1]:0;
C.animate(O,G,D.easing).animate(N,G,D.easing,function(){if(K){E.hide()
}A.effects.restore(E,M);
A.effects.removeWrapper(E);
H()
})
}
}));