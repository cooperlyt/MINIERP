/*
 * jQuery UI Effects Size 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/size-effect/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery","./effect"],A)
}else{A(jQuery)
}}(function(A){return A.effects.effect.size=function(K,J){var O,H,I,B=A(this),N=["position","top","bottom","left","right","width","height","overflow","opacity"],M=["position","top","bottom","left","right","overflow","opacity"],L=["width","height","overflow"],F=["fontSize"],Q=["borderTopWidth","borderBottomWidth","paddingTop","paddingBottom"],C=["borderLeftWidth","borderRightWidth","paddingLeft","paddingRight"],G=A.effects.setMode(B,K.mode||"effect"),P=K.restore||G!=="effect",T=K.scale||"both",R=K.origin||["middle","center"],S=B.css("position"),D=P?N:M,E={height:0,width:0,outerHeight:0,outerWidth:0};
if(G==="show"){B.show()
}O={height:B.height(),width:B.width(),outerHeight:B.outerHeight(),outerWidth:B.outerWidth()};
if(K.mode==="toggle"&&G==="show"){B.from=K.to||E;
B.to=K.from||O
}else{B.from=K.from||(G==="show"?E:O);
B.to=K.to||(G==="hide"?E:O)
}I={from:{y:B.from.height/O.height,x:B.from.width/O.width},to:{y:B.to.height/O.height,x:B.to.width/O.width}};
if(T==="box"||T==="both"){if(I.from.y!==I.to.y){D=D.concat(Q);
B.from=A.effects.setTransition(B,Q,I.from.y,B.from);
B.to=A.effects.setTransition(B,Q,I.to.y,B.to)
}if(I.from.x!==I.to.x){D=D.concat(C);
B.from=A.effects.setTransition(B,C,I.from.x,B.from);
B.to=A.effects.setTransition(B,C,I.to.x,B.to)
}}if(T==="content"||T==="both"){if(I.from.y!==I.to.y){D=D.concat(F).concat(L);
B.from=A.effects.setTransition(B,F,I.from.y,B.from);
B.to=A.effects.setTransition(B,F,I.to.y,B.to)
}}A.effects.save(B,D);
B.show();
A.effects.createWrapper(B);
B.css("overflow","hidden").css(B.from);
if(R){H=A.effects.getBaseline(R,O);
B.from.top=(O.outerHeight-B.outerHeight())*H.y;
B.from.left=(O.outerWidth-B.outerWidth())*H.x;
B.to.top=(O.outerHeight-B.to.outerHeight)*H.y;
B.to.left=(O.outerWidth-B.to.outerWidth)*H.x
}B.css(B.from);
if(T==="content"||T==="both"){Q=Q.concat(["marginTop","marginBottom"]).concat(F);
C=C.concat(["marginLeft","marginRight"]);
L=N.concat(Q).concat(C);
B.find("*[width]").each(function(){var V=A(this),U={height:V.height(),width:V.width(),outerHeight:V.outerHeight(),outerWidth:V.outerWidth()};
if(P){A.effects.save(V,L)
}V.from={height:U.height*I.from.y,width:U.width*I.from.x,outerHeight:U.outerHeight*I.from.y,outerWidth:U.outerWidth*I.from.x};
V.to={height:U.height*I.to.y,width:U.width*I.to.x,outerHeight:U.height*I.to.y,outerWidth:U.width*I.to.x};
if(I.from.y!==I.to.y){V.from=A.effects.setTransition(V,Q,I.from.y,V.from);
V.to=A.effects.setTransition(V,Q,I.to.y,V.to)
}if(I.from.x!==I.to.x){V.from=A.effects.setTransition(V,C,I.from.x,V.from);
V.to=A.effects.setTransition(V,C,I.to.x,V.to)
}V.css(V.from);
V.animate(V.to,K.duration,K.easing,function(){if(P){A.effects.restore(V,L)
}})
})
}B.animate(B.to,{queue:false,duration:K.duration,easing:K.easing,complete:function(){if(B.to.opacity===0){B.css("opacity",B.from.opacity)
}if(G==="hide"){B.hide()
}A.effects.restore(B,D);
if(!P){if(S==="static"){B.css({position:"relative",top:B.to.top,left:B.to.left})
}else{A.each(["top","left"],function(U,V){B.css(V,function(X,Z){var Y=parseInt(Z,10),W=U?B.to.left:B.to.top;
if(Z==="auto"){return W+"px"
}return Y+W+"px"
})
})
}}A.effects.removeWrapper(B);
J()
}})
}
}));