/*
 * jQuery UI Effects Highlight 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/highlight-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.highlight=function(G,B){var D=A(this),C=["backgroundImage","backgroundColor","opacity"],F=A.effects.setMode(D,G.mode||"show"),E={backgroundColor:D.css("backgroundColor")};
if(F==="hide"){E.opacity=0
}A.effects.save(D,C);
D.show().css({backgroundImage:"none",backgroundColor:G.color||"#ffff99"}).animate(E,{queue:false,duration:G.duration,easing:G.easing,complete:function(){if(F==="hide"){D.hide()
}A.effects.restore(D,C);
B()
}})
}
}));