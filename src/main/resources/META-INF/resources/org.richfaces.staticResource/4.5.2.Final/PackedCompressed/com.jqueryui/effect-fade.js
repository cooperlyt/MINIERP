/*
 * jQuery UI Effects Fade 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/fade-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.fade=function(E,B){var C=A(this),D=A.effects.setMode(C,E.mode||"toggle");
C.animate({opacity:D},{queue:false,duration:E.duration,easing:E.easing,complete:B})
}
}));