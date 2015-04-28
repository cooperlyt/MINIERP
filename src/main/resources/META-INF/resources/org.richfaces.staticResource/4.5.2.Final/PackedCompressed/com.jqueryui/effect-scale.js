/*
 * jQuery UI Effects Scale 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/scale-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect","./effect-size"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.scale=function(B,E){var C=A(this),K=A.extend(true,{},B),F=A.effects.setMode(C,B.mode||"effect"),G=parseInt(B.percent,10)||(parseInt(B.percent,10)===0?0:(F==="hide"?0:100)),I=B.direction||"both",J=B.origin,D={height:C.height(),width:C.width(),outerHeight:C.outerHeight(),outerWidth:C.outerWidth()},H={y:I!=="horizontal"?(G/100):1,x:I!=="vertical"?(G/100):1};
K.effect="size";
K.queue=false;
K.complete=E;
if(F!=="effect"){K.origin=J||["middle","center"];
K.restore=true
}K.from=B.from||(F==="show"?{height:0,width:0,outerHeight:0,outerWidth:0}:D);
K.to={height:D.height*H.y,width:D.width*H.x,outerHeight:D.outerHeight*H.y,outerWidth:D.outerWidth*H.x};
if(K.fade){if(F==="show"){K.from.opacity=0;
K.to.opacity=1
}if(F==="hide"){K.from.opacity=1;
K.to.opacity=0
}}C.effect(K)
}
}));