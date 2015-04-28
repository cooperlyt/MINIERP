window.RichFaces=window.RichFaces||{};
RichFaces.jQuery=RichFaces.jQuery||window.jQuery;
(function(F,N){N.csv=N.csv||{};
var H={};
var M=/\'?\{(\d+)\}\'?/g;
var B=function(T,Q,R){if(T){var V=T.replace(M,"\n$1\n").split("\n");
var U;
R[9]=Q;
for(var S=1;
S<V.length;
S+=2){U=R[V[S]];
V[S]=typeof U=="undefined"?"":U
}return V.join("")
}else{return""
}};
var G=function(Q){if(null!==Q.value&&undefined!=Q.value){return Q.value
}else{return""
}};
var L=function(Q){if(Q.checked){return true
}else{return false
}};
var P=function(R,Q){if(Q.selected){return R[R.length]=Q.value
}};
var K={hidden:function(Q){return G(Q)
},text:function(Q){return G(Q)
},textarea:function(Q){return G(Q)
},"select-one":function(Q){if(Q.selectedIndex!=-1){return G(Q)
}},password:function(Q){return G(Q)
},file:function(Q){return G(Q)
},radio:function(Q){return L(Q)
},checkbox:function(Q){return L(Q)
},"select-multiple":function(W){var S=W.name;
var V=W.childNodes;
var U=[];
for(var T=0;
T<V.length;
T++){var X=V[T];
if(X.tagName==="OPTGROUP"){var R=X.childNodes;
for(var Q=0;
Q<R.length;
Q++){U=P(U,R[Q])
}}else{U=P(U,X)
}}return U
},input:function(Q){return G(Q)
}};
var E=function(R){var T="";
if(K[R.type]){T=K[R.type](R)
}else{if(undefined!==R.value){T=R.value
}else{var Q=F(R);
if(Q){if(typeof N.component(Q)["getValue"]==="function"){T=N.component(Q).getValue()
}else{var S=F("*",Q).filter(":editable");
if(S){var U=S[0];
T=K[U.type](U)
}}}}}return T
};
var D=function(Q,R){if(Q.p){return Q.p.label||R
}return R
};
F.extend(N.csv,{RE_DIGITS:/^-?\d+$/,RE_FLOAT:/^(-?\d+)?(\.(\d+)?(e[+-]?\d+)?)?$/,addMessage:function(Q){F.extend(H,Q)
},getMessage:function(T,Q,R,S){var U=T?T:H[S]||{detail:"",summary:"",severity:0};
return{detail:B(U.detail,Q,R),summary:B(U.summary,Q,R),severity:U.severity}
},sendMessage:function(Q,R){N.Event.fire(window.document,N.Event.MESSAGE_EVENT_TYPE,{sourceId:Q,message:R})
},clearMessage:function(Q){N.Event.fire(window.document,N.Event.MESSAGE_EVENT_TYPE,{sourceId:Q})
},validate:function(R,T,a,X){var a=N.getDomElement(a||T);
var d=E(a);
var S;
var V=X.c;
N.csv.clearMessage(T);
if(V){var c=D(V,T);
try{if(V.f){S=V.f(d,T,D(V,T),V.m)
}}catch(b){b.severity=2;
N.csv.sendMessage(T,b);
return false
}}else{S=d
}var f=true;
var W=X.v;
var Z;
if(W){var U,Q;
for(var Y=0;
Y<W.length;
Y++){try{Q=W[Y];
U=Q.f;
if(U){U(S,D(Q,T),Q.p,Q.m)
}}catch(b){Z=b;
b.severity=2;
N.csv.sendMessage(T,b);
f=false
}}}if(!f&&X.oninvalid instanceof Function){X.oninvalid([Z])
}if(f){if(!X.da&&X.a){X.a.call(a,R,T)
}else{if(X.onvalid instanceof Function){X.onvalid()
}}}return f
}});
var J=function(W,T,X,U,R,V){var Q=null,S=W;
if(W){W=F.trim(W);
if(!N.csv.RE_DIGITS.test(W)||(Q=parseInt(W,10))<U||Q>R){throw N.csv.getMessage(X,S,V?[W,V,T]:[W,T])
}}return Q
};
var A=function(U,S,V,T){var Q=null,R=U;
if(U){U=F.trim(U);
if(!N.csv.RE_FLOAT.test(U)||isNaN(Q=parseFloat(U))){throw N.csv.getMessage(V,R,T?[U,T,S]:[U,S])
}}return Q
};
F.extend(N.csv,{convertBoolean:function(S,Q,U,T){if(typeof S==="string"){var R=F.trim(S).toLowerCase();
if(R==="on"||R==="true"||R==="yes"){return true
}}else{if(true===S){return true
}}return false
},convertDate:function(S,R,U,T){var Q;
S=F.trim(S);
Q=Date.parse(S);
return Q
},convertByte:function(R,Q,T,S){return J(R,Q,S,-128,127,254)
},convertNumber:function(T,S,V,U){var Q,R=T;
T=F.trim(T);
Q=parseFloat(T);
if(isNaN(Q)){throw N.csv.getMessage(U,R,[T,99,S])
}return Q
},convertFloat:function(R,Q,T,S){return A(R,Q,S,2000000000)
},convertDouble:function(R,Q,T,S){return A(R,Q,S,1999999)
},convertShort:function(R,Q,T,S){return J(R,Q,S,-32768,32767,32456)
},convertInteger:function(R,Q,T,S){return J(R,Q,S,-2147483648,2147483648,9346)
},convertCharacter:function(R,Q,T,S){return J(R,Q,S,0,65535)
},convertLong:function(R,Q,T,S){return J(R,Q,S,-9223372036854776000,9223372036854776000,98765432)
}});
var O=function(Q,S,R,W,V){var U=typeof W.min==="number";
var T=typeof W.max==="number";
if(T&&S>W.max){throw N.csv.getMessage(V,Q,U?[W.min,W.max,R]:[W.max,R])
}if(U&&S<W.min){throw N.csv.getMessage(V,Q,T?[W.min,W.max,R]:[W.min,R])
}};
var C=function(U,Q,T,W){if(typeof T!="string"||T.length==0){throw N.csv.getMessage(W,U,[],"REGEX_VALIDATOR_PATTERN_NOT_SET")
}var S=I(T);
var R;
try{R=new RegExp(S)
}catch(V){throw N.csv.getMessage(W,U,[],"REGEX_VALIDATOR_MATCH_EXCEPTION")
}if(!R.test(U)){throw N.csv.getMessage(W,U,[T,Q])
}};
var I=function(Q){if(!(Q.slice(0,1)==="^")){Q="^"+Q
}if(!(Q.slice(-1)==="$")){Q=Q+"$"
}return Q
};
F.extend(N.csv,{validateLongRange:function(T,R,V,U){var S=typeof T,Q=T;
if(S!=="number"){if(S!="string"){throw N.csv.getMessage(U,T,[componentId,""],"LONG_RANGE_VALIDATOR_TYPE")
}else{T=F.trim(T);
if(!N.csv.RE_DIGITS.test(T)||(T=parseInt(T,10))==NaN){throw N.csv.getMessage(U,T,[componentId,""],"LONG_RANGE_VALIDATOR_TYPE")
}}}O(Q,T,R,V,U)
},validateDoubleRange:function(T,R,V,U){var S=typeof T,Q=T;
if(S!=="number"){if(S!=="string"){throw N.csv.getMessage(U,T,[componentId,""],"DOUBLE_RANGE_VALIDATOR_TYPE")
}else{T=F.trim(T);
if(!N.csv.RE_FLOAT.test(T)||(T=parseFloat(T))==NaN){throw N.csv.getMessage(U,T,[componentId,""],"DOUBLE_RANGE_VALIDATOR_TYPE")
}}}O(Q,T,R,V,U)
},validateLength:function(S,Q,U,T){var R=S?S.length:0;
O(S,R,Q,U,T)
},validateSize:function(S,Q,U,T){var R=S?S.length:0;
O(S,R,Q,U,T)
},validateRegex:function(R,Q,T,S){C(R,Q,T.pattern,S)
},validatePattern:function(R,Q,T,S){C(R,Q,T.regexp,S)
},validateRequired:function(R,Q,T,S){if(undefined===R||null===R||""===R){throw N.csv.getMessage(S,R,[Q])
}},validateTrue:function(R,Q,T,S){if(R!==true){throw S
}},validateFalse:function(R,Q,T,S){if(R!==false){throw S
}},validateMax:function(R,Q,T,S){if(R>T.value){throw S
}},validateMin:function(R,Q,T,S){if(R<T.value){throw S
}}})
})(RichFaces.jQuery,RichFaces);