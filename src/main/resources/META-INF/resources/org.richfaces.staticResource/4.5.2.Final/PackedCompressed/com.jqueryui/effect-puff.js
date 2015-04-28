/*
 * jQuery UI Effects Puff 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/puff-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect","./effect-scale"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.puff=function(I,B){var G=A(this),H=A.effects.setMode(G,I.mode||"hide"),E=H==="hide",F=parseInt(I.percent,10)||150,D=F/100,C={height:G.height(),width:G.width(),outerHeight:G.outerHeight(),outerWidth:G.outerWidth()};
A.extend(I,{effect:"scale",queue:false,fade:true,mode:H,complete:B,percent:E?F:100,from:E?C:{height:C.height*D,width:C.width*D,outerHeight:C.outerHeight*D,outerWidth:C.outerWidth*D}});
G.effect(I)
}
}));