(function(D,I){var J,A;
var E;
var B;
D.extend({pnotify_remove_all:function(){var L=E.data("pnotify");
if(L&&L.length){D.each(L,function(){if(this.pnotify_remove){this.pnotify_remove()
}})
}},pnotify_position_all:function(){if(A){clearTimeout(A)
}A=null;
var L=E.data("pnotify");
if(!L||!L.length){return 
}D.each(L,function(){var P=this.opts.pnotify_stack;
if(!P){return 
}if(!P.nextpos1){P.nextpos1=P.firstpos1
}if(!P.nextpos2){P.nextpos2=P.firstpos2
}if(!P.addpos2){P.addpos2=0
}if(this.css("display")!="none"){var R,Q;
var M={};
var O;
switch(P.dir1){case"down":O="top";
break;
case"up":O="bottom";
break;
case"left":O="right";
break;
case"right":O="left";
break
}R=parseInt(this.css(O));
if(isNaN(R)){R=0
}if(typeof P.firstpos1=="undefined"){P.firstpos1=R;
P.nextpos1=P.firstpos1
}var N;
switch(P.dir2){case"down":N="top";
break;
case"up":N="bottom";
break;
case"left":N="right";
break;
case"right":N="left";
break
}Q=parseInt(this.css(N));
if(isNaN(Q)){Q=0
}if(typeof P.firstpos2=="undefined"){P.firstpos2=Q;
P.nextpos2=P.firstpos2
}if((P.dir1=="down"&&P.nextpos1+this.height()>B.height())||(P.dir1=="up"&&P.nextpos1+this.height()>B.height())||(P.dir1=="left"&&P.nextpos1+this.width()>B.width())||(P.dir1=="right"&&P.nextpos1+this.width()>B.width())){P.nextpos1=P.firstpos1;
P.nextpos2+=P.addpos2+10;
P.addpos2=0
}if(P.animation&&P.nextpos2<Q){switch(P.dir2){case"down":M.top=P.nextpos2+"px";
break;
case"up":M.bottom=P.nextpos2+"px";
break;
case"left":M.right=P.nextpos2+"px";
break;
case"right":M.left=P.nextpos2+"px";
break
}}else{this.css(N,P.nextpos2+"px")
}switch(P.dir2){case"down":case"up":if(this.outerHeight(true)>P.addpos2){P.addpos2=this.height()
}break;
case"left":case"right":if(this.outerWidth(true)>P.addpos2){P.addpos2=this.width()
}break
}if(P.nextpos1){if(P.animation&&(R>P.nextpos1||M.top||M.bottom||M.right||M.left)){switch(P.dir1){case"down":M.top=P.nextpos1+"px";
break;
case"up":M.bottom=P.nextpos1+"px";
break;
case"left":M.right=P.nextpos1+"px";
break;
case"right":M.left=P.nextpos1+"px";
break
}}else{this.css(O,P.nextpos1+"px")
}}if(M.top||M.bottom||M.right||M.left){this.animate(M,{duration:500,queue:false})
}switch(P.dir1){case"down":case"up":P.nextpos1+=this.height()+10;
break;
case"left":case"right":P.nextpos1+=this.width()+10;
break
}}});
D.each(L,function(){var M=this.opts.pnotify_stack;
if(!M){return 
}M.nextpos1=M.firstpos1;
M.nextpos2=M.firstpos2;
M.addpos2=0;
M.animation=true
})
},pnotify:function(S){if(!E){E=D("body")
}if(!B){B=D(window)
}var T;
var L;
if(typeof S!="object"){L=D.extend({},D.pnotify.defaults);
L.pnotify_text=S
}else{L=D.extend({},D.pnotify.defaults,S);
if(L.pnotify_animation instanceof Object){L.pnotify_animation=D.extend({effect_in:D.pnotify.defaults.pnotify_animation,effect_out:D.pnotify.defaults.pnotify_animation},L.pnotify_animation)
}}if(L.pnotify_before_init){if(L.pnotify_before_init(L)===false){return null
}}var M;
var N=function(Y,V){P.css("display","none");
var U=document.elementFromPoint(Y.clientX,Y.clientY);
P.css("display","block");
var X=D(U);
var W=X.css("cursor");
P.css("cursor",W!="auto"?W:"default");
if(!M||M.get(0)!=U){if(M){F.call(M.get(0),"mouseleave",Y.originalEvent);
F.call(M.get(0),"mouseout",Y.originalEvent)
}F.call(U,"mouseenter",Y.originalEvent);
F.call(U,"mouseover",Y.originalEvent)
}F.call(U,V,Y.originalEvent);
M=X
};
var P=D("<div />",{"class":"rf-ntf "+L.pnotify_addclass,css:{display:"none"},mouseenter:function(U){if(L.pnotify_nonblock){U.stopPropagation()
}if(L.pnotify_mouse_reset&&T=="out"){P.stop(true);
T="in";
P.css("height","auto").animate({width:L.pnotify_width,opacity:L.pnotify_nonblock?L.pnotify_nonblock_opacity:L.pnotify_opacity},"fast")
}if(L.pnotify_nonblock){P.animate({opacity:L.pnotify_nonblock_opacity},"fast")
}if(L.pnotify_hide&&L.pnotify_mouse_reset){P.pnotify_cancel_remove()
}if(L.pnotify_closer&&!L.pnotify_nonblock){P.closer.css("visibility","visible")
}},mouseleave:function(U){if(L.pnotify_nonblock){U.stopPropagation()
}M=null;
P.css("cursor","auto");
if(L.pnotify_nonblock&&T!="out"){P.animate({opacity:L.pnotify_opacity},"fast")
}if(L.pnotify_hide&&L.pnotify_mouse_reset){P.pnotify_queue_remove()
}P.closer.css("visibility","hidden");
D.pnotify_position_all()
},mouseover:function(U){if(L.pnotify_nonblock){U.stopPropagation()
}},mouseout:function(U){if(L.pnotify_nonblock){U.stopPropagation()
}},mousemove:function(U){if(L.pnotify_nonblock){U.stopPropagation();
N(U,"onmousemove")
}},mousedown:function(U){if(L.pnotify_nonblock){U.stopPropagation();
U.preventDefault();
N(U,"onmousedown")
}},mouseup:function(U){if(L.pnotify_nonblock){U.stopPropagation();
U.preventDefault();
N(U,"onmouseup")
}},click:function(U){if(L.pnotify_nonblock){U.stopPropagation();
N(U,"onclick")
}},dblclick:function(U){if(L.pnotify_nonblock){U.stopPropagation();
N(U,"ondblclick")
}}});
P.opts=L;
if(L.pnotify_shadow&&!I.browser.msie){P.shadow_container=D("<div />",{"class":"rf-ntf-shdw"}).prependTo(P)
}P.container=D("<div />",{"class":"rf-ntf-cnt"}).appendTo(P);
P.pnotify_version="1.0.2";
P.pnotify=function(U){var V=L;
if(typeof U=="string"){L.pnotify_text=U
}else{L=D.extend({},L,U)
}P.opts=L;
if(L.pnotify_shadow!=V.pnotify_shadow){if(L.pnotify_shadow&&!I.browser.msie){P.shadow_container=D("<div />",{"class":"rf-ntf-shdw"}).prependTo(P)
}else{P.children(".rf-ntf-shdw").remove()
}}if(L.pnotify_addclass===false){P.removeClass(V.pnotify_addclass)
}else{if(L.pnotify_addclass!==V.pnotify_addclass){P.removeClass(V.pnotify_addclass).addClass(L.pnotify_addclass)
}}if(L.pnotify_title===false){P.title_container.hide("fast")
}else{if(L.pnotify_title!==V.pnotify_title){P.title_container.html(L.pnotify_title).show(200)
}}if(L.pnotify_text===false){P.text_container.hide("fast")
}else{if(L.pnotify_text!==V.pnotify_text){if(L.pnotify_insert_brs){L.pnotify_text=L.pnotify_text.replace(/\n/g,"<br />")
}P.text_container.html(L.pnotify_text).show(200)
}}P.pnotify_history=L.pnotify_history;
if(L.pnotify_type!=V.pnotify_type){P.container.toggleClass("rf-ntf-cnt rf-ntf-cnt-hov")
}if((L.pnotify_notice_icon!=V.pnotify_notice_icon&&L.pnotify_type=="notice")||(L.pnotify_error_icon!=V.pnotify_error_icon&&L.pnotify_type=="error")||(L.pnotify_type!=V.pnotify_type)){P.container.find("div.rf-ntf-ico").remove();
D("<div />",{"class":"rf-ntf-ico"}).append(D("<span />",{"class":L.pnotify_type=="error"?L.pnotify_error_icon:L.pnotify_notice_icon})).prependTo(P.container)
}if(L.pnotify_width!==V.pnotify_width){P.animate({width:L.pnotify_width})
}if(L.pnotify_min_height!==V.pnotify_min_height){P.container.animate({minHeight:L.pnotify_min_height})
}if(L.pnotify_opacity!==V.pnotify_opacity){P.fadeTo(L.pnotify_animate_speed,L.pnotify_opacity)
}if(!L.pnotify_hide){P.pnotify_cancel_remove()
}else{if(!V.pnotify_hide){P.pnotify_queue_remove()
}}P.pnotify_queue_position();
return P
};
P.pnotify_queue_position=function(){if(A){clearTimeout(A)
}A=setTimeout(D.pnotify_position_all,10)
};
P.pnotify_display=function(){if(!P.parent().length){P.appendTo(E)
}if(L.pnotify_before_open){if(L.pnotify_before_open(P)===false){return 
}}P.pnotify_queue_position();
if(L.pnotify_animation=="fade"||L.pnotify_animation.effect_in=="fade"){P.show().fadeTo(0,0).hide()
}else{if(L.pnotify_opacity!=1){P.show().fadeTo(0,L.pnotify_opacity).hide()
}}P.animate_in(function(){if(L.pnotify_after_open){L.pnotify_after_open(P)
}P.pnotify_queue_position();
if(L.pnotify_hide){P.pnotify_queue_remove()
}})
};
P.pnotify_remove=function(){if(P.timer){window.clearTimeout(P.timer);
P.timer=null
}if(L.pnotify_before_close){if(L.pnotify_before_close(P)===false){return 
}}P.animate_out(function(){if(L.pnotify_after_close){if(L.pnotify_after_close(P)===false){return 
}}P.pnotify_queue_position();
if(L.pnotify_remove){P.detach()
}})
};
P.animate_in=function(V){T="in";
var U;
if(typeof L.pnotify_animation.effect_in!="undefined"){U=L.pnotify_animation.effect_in
}else{U=L.pnotify_animation
}if(U=="none"){P.show();
V()
}else{if(U=="show"){P.show(L.pnotify_animate_speed,V)
}else{if(U=="fade"){P.show().fadeTo(L.pnotify_animate_speed,L.pnotify_opacity,V)
}else{if(U=="slide"){P.slideDown(L.pnotify_animate_speed,V)
}else{if(typeof U=="function"){U("in",V,P)
}else{if(P.effect){P.effect(U,{},L.pnotify_animate_speed,V)
}}}}}}};
P.animate_out=function(V){T="out";
var U;
if(typeof L.pnotify_animation.effect_out!="undefined"){U=L.pnotify_animation.effect_out
}else{U=L.pnotify_animation
}if(U=="none"){P.hide();
V()
}else{if(U=="show"){P.hide(L.pnotify_animate_speed,V)
}else{if(U=="fade"){P.fadeOut(L.pnotify_animate_speed,V)
}else{if(U=="slide"){P.slideUp(L.pnotify_animate_speed,V)
}else{if(typeof U=="function"){U("out",V,P)
}else{if(P.effect){P.effect(U,{},L.pnotify_animate_speed,V)
}}}}}}};
P.pnotify_cancel_remove=function(){if(P.timer){window.clearTimeout(P.timer)
}};
P.pnotify_queue_remove=function(){P.pnotify_cancel_remove();
P.timer=window.setTimeout(function(){P.pnotify_remove()
},(isNaN(L.pnotify_delay)?0:L.pnotify_delay))
};
P.closer=D("<div />",{"class":"rf-ntf-cls",css:{cursor:"pointer",visibility:"hidden"},click:function(){P.pnotify_remove();
P.closer.css("visibility","hidden")
}}).append(D("<span />",{"class":"rf-ntf-cls-ico"})).appendTo(P.container);
D("<div />",{"class":"rf-ntf-ico"}).append(D("<span />",{"class":L.pnotify_type=="error"?L.pnotify_error_icon:L.pnotify_notice_icon})).appendTo(P.container);
P.title_container=D("<div />",{"class":"rf-ntf-sum",html:L.pnotify_title}).appendTo(P.container);
if(L.pnotify_title===false){P.title_container.hide()
}if(L.pnotify_insert_brs&&typeof L.pnotify_text=="string"){L.pnotify_text=L.pnotify_text.replace(/\n/g,"<br />")
}P.text_container=D("<div />",{"class":"rf-ntf-det",html:L.pnotify_text}).appendTo(P.container);
if(L.pnotify_text===false){P.text_container.hide()
}D("<div />",{"class":"rf-ntf-clr"}).appendTo(P.container);
if(typeof L.pnotify_width=="string"){P.css("width",L.pnotify_width)
}if(typeof L.pnotify_min_height=="string"){P.container.css("min-height",L.pnotify_min_height)
}P.pnotify_history=L.pnotify_history;
var R=E.data("pnotify");
if(R==null||typeof R!="object"){R=[]
}if(L.pnotify_stack.push=="top"){R=D.merge([P],R)
}else{R=D.merge(R,[P])
}E.data("pnotify",R);
if(L.pnotify_after_init){L.pnotify_after_init(P)
}if(L.pnotify_history){var Q=E.data("pnotify_history");
if(typeof Q=="undefined"){Q=D("<div />",{"class":"rf-ntf-hstr",mouseleave:function(){Q.animate({top:"-"+J+"px"},{duration:100,queue:false})
}}).append(D("<div />",{"class":"rf-ntf-hstr-hdr",text:"Redisplay"})).append(D("<button />",{"class":"rf-ntf-hstr-all",text:"All",click:function(){D.each(E.data("pnotify"),function(){if(this.pnotify_history&&this.pnotify_display){this.pnotify_display()
}});
return false
}})).append(D("<button />",{"class":"rf-ntf-hstr-last",text:"Last",click:function(){var U=1;
var V=E.data("pnotify");
while(!V[V.length-U]||!V[V.length-U].pnotify_history||V[V.length-U].is(":visible")){if(V.length-U===0){return false
}U++
}var W=V[V.length-U];
if(W.pnotify_display){W.pnotify_display()
}return false
}})).appendTo(E);
var O=D("<span />",{"class":"rf-ntf-hstr-hndl",mouseenter:function(){Q.animate({top:"0"},{duration:100,queue:false})
}}).appendTo(Q);
J=O.offset().top+2;
Q.css({top:"-"+J+"px"});
E.data("pnotify_history",Q)
}}L.pnotify_stack.animation=false;
P.pnotify_display();
return P
}});
var K=/^on/;
var C=/^(dbl)?click$|^mouse(move|down|up|over|out|enter|leave)$|^contextmenu$/;
var H=/^(focus|blur|select|change|reset)$|^key(press|down|up)$/;
var G=/^(scroll|resize|(un)?load|abort|error)$/;
var F=function(M,L){var N;
M=M.toLowerCase();
if(document.createEvent&&this.dispatchEvent){M=M.replace(K,"");
if(M.match(C)){D(this).offset();
N=document.createEvent("MouseEvents");
N.initMouseEvent(M,L.bubbles,L.cancelable,L.view,L.detail,L.screenX,L.screenY,L.clientX,L.clientY,L.ctrlKey,L.altKey,L.shiftKey,L.metaKey,L.button,L.relatedTarget)
}else{if(M.match(H)){N=document.createEvent("UIEvents");
N.initUIEvent(M,L.bubbles,L.cancelable,L.view,L.detail)
}else{if(M.match(G)){N=document.createEvent("HTMLEvents");
N.initEvent(M,L.bubbles,L.cancelable)
}}}if(!N){return 
}this.dispatchEvent(N)
}else{if(!M.match(K)){M="on"+M
}N=document.createEventObject(L);
this.fireEvent(M,N)
}};
D.pnotify.defaults={pnotify_title:false,pnotify_text:false,pnotify_addclass:"",pnotify_nonblock:false,pnotify_nonblock_opacity:0.2,pnotify_history:true,pnotify_width:"300px",pnotify_min_height:"16px",pnotify_type:"notice",pnotify_notice_icon:"",pnotify_error_icon:"",pnotify_animation:"fade",pnotify_animate_speed:"slow",pnotify_opacity:1,pnotify_shadow:false,pnotify_closer:true,pnotify_hide:true,pnotify_delay:8000,pnotify_mouse_reset:true,pnotify_remove:true,pnotify_insert_brs:true,pnotify_stack:{dir1:"down",dir2:"left",push:"bottom"}}
})(jQuery,RichFaces);