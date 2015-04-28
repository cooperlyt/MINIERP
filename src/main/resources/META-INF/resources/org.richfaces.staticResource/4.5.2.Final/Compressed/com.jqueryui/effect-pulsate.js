/*
 * jQuery UI Effects Pulsate 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/pulsate-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.pulsate=function(B,F){var D=A(this),I=A.effects.setMode(D,B.mode||"show"),M=I==="show",J=I==="hide",N=(M||I==="hide"),K=((B.times||5)*2)+(N?1:0),E=B.duration/K,L=0,H=D.queue(),C=H.length,G;
if(M||!D.is(":visible")){D.css("opacity",0).show();
L=1
}for(G=1;
G<K;
G++){D.animate({opacity:L},E,B.easing);
L=1-L
}D.animate({opacity:L},E,B.easing);
D.queue(function(){if(J){D.hide()
}F()
});
if(C>1){H.splice.apply(H,[1,0].concat(H.splice(C,K+1)))
}D.dequeue()
}
}));