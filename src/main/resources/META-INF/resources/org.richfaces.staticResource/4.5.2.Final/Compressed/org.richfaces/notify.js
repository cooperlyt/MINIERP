(function(F,I){I.ui=I.ui||{};
var E={styleClass:"",nonblocking:false,nonblockingOpacity:0.2,showHistory:false,animationSpeed:"slow",opacity:"1",showShadow:false,showCloseButton:true,appearAnimation:"fade",hideAnimation:"fade",sticky:false,stayTime:8000,delay:0};
var H="org.richfaces.notifyStack.default";
var J="click dblclick  keydown keypress keyup mousedown mousemove mouseout mouseover mouseup";
var K={summary:"pnotify_title",detail:"pnotify_text",styleClass:"pnotify_addclass",nonblocking:"pnotify_nonblock",nonblockingOpacity:"pnotify_nonblock_opacity",showHistory:"pnotify_history",animation:"pnotify_animation",appearAnimation:"effect_in",hideAnimation:"effect_out",animationSpeed:"pnotify_animate_speed",opacity:"pnotify_opacity",showShadow:"pnotify_shadow",showCloseButton:"pnotify_closer",sticky:"pnotify_hide",stayTime:"pnotify_delay"};
var B=["rf-ntf-inf","rf-ntf-wrn","rf-ntf-err","rf-ntf-ftl"];
var G=function(O,N,P){for(var L in N){var M=P[L]!=null?P[L]:L;
O[M]=N[L];
if(O[M] instanceof Object){O[M]=F.extend({},O[M],P)
}}return O
};
var D=function(){if(!document.getElementById(H)){var L=F('<span id="'+H+'" class="rf-ntf-stck" />');
F("body").append(L);
new I.ui.NotifyStack(H)
}return C(H)
};
var C=function(L){if(!L){return D()
}return I.component(L).getStack()
};
var A=function(O,N,M){var L=O.slice((M||N)+1||O.length);
O.length=N<0?O.length+N:N;
return O.push.apply(O,L)
};
I.ui.Notify=function(M){var M=F.extend({},E,M);
if(typeof M.severity=="number"){var L=B[M.severity];
M.styleClass=M.styleClass?L+" "+M.styleClass:L
}var N=G({},M,K);
var O=function(){var P=C(M.stackId);
N.pnotify_stack=P;
N.pnotify_addclass+=" rf-ntf-pos-"+P.position;
N.pnotify_after_close=function(R){var S=F.inArray(R,P.notifications);
if(S>=0){A(P.notifications,S)
}};
var Q=F.pnotify(N);
Q.on(J,function(R){if(M["on"+R.type]){M["on"+R.type].call(this,R)
}});
P.addNotification(Q)
};
if(M.sticky!==null){N.pnotify_hide=!M.sticky
}F(document).ready(function(){if(M.delay){setTimeout(function(){O()
},M.delay)
}else{O()
}})
}
})(RichFaces.jQuery,RichFaces);