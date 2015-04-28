/*
 * jQuery UI Effects Transfer 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/transfer-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.transfer=function(C,G){var E=A(this),J=A(C.to),M=J.css("position")==="fixed",I=A("body"),K=M?I.scrollTop():0,L=M?I.scrollLeft():0,B=J.offset(),F={top:B.top-K,left:B.left-L,height:J.innerHeight(),width:J.innerWidth()},H=E.offset(),D=A("<div class='ui-effects-transfer'></div>").appendTo(document.body).addClass(C.className).css({top:H.top-K,left:H.left-L,height:E.innerHeight(),width:E.innerWidth(),position:M?"fixed":"absolute"}).animate(F,C.duration,C.easing,function(){D.remove();
G()
})
}
}));