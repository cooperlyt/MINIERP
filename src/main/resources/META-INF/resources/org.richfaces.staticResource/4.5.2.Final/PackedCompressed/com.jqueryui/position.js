/*
 * jQuery UI Position 1.11.2
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/position/
 */
(function(A){if(typeof define==="function"&&define.amd){define(["jquery"],A)
}else{A(jQuery)
}}(function(A){(function(){A.ui=A.ui||{};
var I,L,J=Math.max,O=Math.abs,M=Math.round,D=/left|center|right/,G=/top|center|bottom/,B=/[\+\-]\d+(\.[\d]+)?%?/,K=/^\w+/,C=/%$/,F=A.fn.position;
function N(R,Q,P){return[parseFloat(R[0])*(C.test(R[0])?Q/100:1),parseFloat(R[1])*(C.test(R[1])?P/100:1)]
}function H(P,Q){return parseInt(A.css(P,Q),10)||0
}function E(Q){var P=Q[0];
if(P.nodeType===9){return{width:Q.width(),height:Q.height(),offset:{top:0,left:0}}
}if(A.isWindow(P)){return{width:Q.width(),height:Q.height(),offset:{top:Q.scrollTop(),left:Q.scrollLeft()}}
}if(P.preventDefault){return{width:0,height:0,offset:{top:P.pageY,left:P.pageX}}
}return{width:Q.outerWidth(),height:Q.outerHeight(),offset:Q.offset()}
}A.position={scrollbarWidth:function(){if(I!==undefined){return I
}var Q,P,S=A("<div style='display:block;position:absolute;width:50px;height:50px;overflow:hidden;'><div style='height:100px;width:auto;'></div></div>"),R=S.children()[0];
A("body").append(S);
Q=R.offsetWidth;
S.css("overflow","scroll");
P=R.offsetWidth;
if(Q===P){P=S[0].clientWidth
}S.remove();
return(I=Q-P)
},getScrollInfo:function(T){var S=T.isWindow||T.isDocument?"":T.element.css("overflow-x"),R=T.isWindow||T.isDocument?"":T.element.css("overflow-y"),Q=S==="scroll"||(S==="auto"&&T.width<T.element[0].scrollWidth),P=R==="scroll"||(R==="auto"&&T.height<T.element[0].scrollHeight);
return{width:P?A.position.scrollbarWidth():0,height:Q?A.position.scrollbarWidth():0}
},getWithinInfo:function(Q){var R=A(Q||window),P=A.isWindow(R[0]),S=!!R[0]&&R[0].nodeType===9;
return{element:R,isWindow:P,isDocument:S,offset:R.offset()||{left:0,top:0},scrollLeft:R.scrollLeft(),scrollTop:R.scrollTop(),width:P||S?R.width():R.outerWidth(),height:P||S?R.height():R.outerHeight()}
}};
A.fn.position=function(Z){if(!Z||!Z.of){return F.apply(this,arguments)
}Z=A.extend({},Z);
var a,W,U,Y,T,P,V=A(Z.of),S=A.position.getWithinInfo(Z.within),Q=A.position.getScrollInfo(S),X=(Z.collision||"flip").split(" "),R={};
P=E(V);
if(V[0].preventDefault){Z.at="left top"
}W=P.width;
U=P.height;
Y=P.offset;
T=A.extend({},Y);
A.each(["my","at"],function(){var d=(Z[this]||"").split(" "),c,b;
if(d.length===1){d=D.test(d[0])?d.concat(["center"]):G.test(d[0])?["center"].concat(d):["center","center"]
}d[0]=D.test(d[0])?d[0]:"center";
d[1]=G.test(d[1])?d[1]:"center";
c=B.exec(d[0]);
b=B.exec(d[1]);
R[this]=[c?c[0]:0,b?b[0]:0];
Z[this]=[K.exec(d[0])[0],K.exec(d[1])[0]]
});
if(X.length===1){X[1]=X[0]
}if(Z.at[0]==="right"){T.left+=W
}else{if(Z.at[0]==="center"){T.left+=W/2
}}if(Z.at[1]==="bottom"){T.top+=U
}else{if(Z.at[1]==="center"){T.top+=U/2
}}a=N(R.at,W,U);
T.left+=a[0];
T.top+=a[1];
return this.each(function(){var c,l,e=A(this),g=e.outerWidth(),d=e.outerHeight(),f=H(this,"marginLeft"),b=H(this,"marginTop"),k=g+f+H(this,"marginRight")+Q.width,j=d+b+H(this,"marginBottom")+Q.height,h=A.extend({},T),i=N(R.my,e.outerWidth(),e.outerHeight());
if(Z.my[0]==="right"){h.left-=g
}else{if(Z.my[0]==="center"){h.left-=g/2
}}if(Z.my[1]==="bottom"){h.top-=d
}else{if(Z.my[1]==="center"){h.top-=d/2
}}h.left+=i[0];
h.top+=i[1];
if(!L){h.left=M(h.left);
h.top=M(h.top)
}c={marginLeft:f,marginTop:b};
A.each(["left","top"],function(n,m){if(A.ui.position[X[n]]){A.ui.position[X[n]][m](h,{targetWidth:W,targetHeight:U,elemWidth:g,elemHeight:d,collisionPosition:c,collisionWidth:k,collisionHeight:j,offset:[a[0]+i[0],a[1]+i[1]],my:Z.my,at:Z.at,within:S,elem:e})
}});
if(Z.using){l=function(p){var r=Y.left-h.left,o=r+W-g,q=Y.top-h.top,n=q+U-d,m={target:{element:V,left:Y.left,top:Y.top,width:W,height:U},element:{element:e,left:h.left,top:h.top,width:g,height:d},horizontal:o<0?"left":r>0?"right":"center",vertical:n<0?"top":q>0?"bottom":"middle"};
if(W<g&&O(r+o)<W){m.horizontal="center"
}if(U<d&&O(q+n)<U){m.vertical="middle"
}if(J(O(r),O(o))>J(O(q),O(n))){m.important="horizontal"
}else{m.important="vertical"
}Z.using.call(this,p,m)
}
}e.offset(A.extend(h,{using:l}))
})
};
A.ui.position={fit:{left:function(T,S){var R=S.within,V=R.isWindow?R.scrollLeft:R.offset.left,X=R.width,U=T.left-S.collisionPosition.marginLeft,W=V-U,Q=U+S.collisionWidth-X-V,P;
if(S.collisionWidth>X){if(W>0&&Q<=0){P=T.left+W+S.collisionWidth-X-V;
T.left+=W-P
}else{if(Q>0&&W<=0){T.left=V
}else{if(W>Q){T.left=V+X-S.collisionWidth
}else{T.left=V
}}}}else{if(W>0){T.left+=W
}else{if(Q>0){T.left-=Q
}else{T.left=J(T.left-U,T.left)
}}}},top:function(S,R){var Q=R.within,W=Q.isWindow?Q.scrollTop:Q.offset.top,X=R.within.height,U=S.top-R.collisionPosition.marginTop,V=W-U,T=U+R.collisionHeight-X-W,P;
if(R.collisionHeight>X){if(V>0&&T<=0){P=S.top+V+R.collisionHeight-X-W;
S.top+=V-P
}else{if(T>0&&V<=0){S.top=W
}else{if(V>T){S.top=W+X-R.collisionHeight
}else{S.top=W
}}}}else{if(V>0){S.top+=V
}else{if(T>0){S.top-=T
}else{S.top=J(S.top-U,S.top)
}}}}},flip:{left:function(V,U){var T=U.within,Z=T.offset.left+T.scrollLeft,c=T.width,R=T.isWindow?T.scrollLeft:T.offset.left,W=V.left-U.collisionPosition.marginLeft,a=W-R,Q=W+U.collisionWidth-c-R,Y=U.my[0]==="left"?-U.elemWidth:U.my[0]==="right"?U.elemWidth:0,b=U.at[0]==="left"?U.targetWidth:U.at[0]==="right"?-U.targetWidth:0,S=-2*U.offset[0],P,X;
if(a<0){P=V.left+Y+b+S+U.collisionWidth-c-Z;
if(P<0||P<O(a)){V.left+=Y+b+S
}}else{if(Q>0){X=V.left-U.collisionPosition.marginLeft+Y+b+S-R;
if(X>0||O(X)<Q){V.left+=Y+b+S
}}}},top:function(U,T){var S=T.within,b=S.offset.top+S.scrollTop,c=S.height,P=S.isWindow?S.scrollTop:S.offset.top,W=U.top-T.collisionPosition.marginTop,Y=W-P,V=W+T.collisionHeight-c-P,Z=T.my[1]==="top",X=Z?-T.elemHeight:T.my[1]==="bottom"?T.elemHeight:0,d=T.at[1]==="top"?T.targetHeight:T.at[1]==="bottom"?-T.targetHeight:0,R=-2*T.offset[1],a,Q;
if(Y<0){Q=U.top+X+d+R+T.collisionHeight-c-b;
if((U.top+X+d+R)>Y&&(Q<0||Q<O(Y))){U.top+=X+d+R
}}else{if(V>0){a=U.top-T.collisionPosition.marginTop+X+d+R-P;
if((U.top+X+d+R)>V&&(a>0||O(a)<V)){U.top+=X+d+R
}}}}},flipfit:{left:function(){A.ui.position.flip.left.apply(this,arguments);
A.ui.position.fit.left.apply(this,arguments)
},top:function(){A.ui.position.flip.top.apply(this,arguments);
A.ui.position.fit.top.apply(this,arguments)
}}};
(function(){var T,V,Q,S,R,P=document.getElementsByTagName("body")[0],U=document.createElement("div");
T=document.createElement(P?"div":"body");
Q={visibility:"hidden",width:0,height:0,border:0,margin:0,background:"none"};
if(P){A.extend(Q,{position:"absolute",left:"-1000px",top:"-1000px"})
}for(R in Q){T.style[R]=Q[R]
}T.appendChild(U);
V=P||document.documentElement;
V.insertBefore(T,V.firstChild);
U.style.cssText="position: absolute; left: 10.7432222px;";
S=A(U).offset().left;
L=S>10&&S<11;
T.innerHTML="";
V.removeChild(T)
})()
})();
return A.ui.position
}));