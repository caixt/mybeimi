cc.Class({
    extends: cc.Component,

    properties: {
        loaddingPrefab: {
            default: null,
            type: cc.Prefab
        },
        alertPrefab: {
            default: null,
            type: cc.Prefab
        }
    },

    // use this for initialization
    onLoad: function () {
        if(!cc.sys.isNative && cc.sys.isMobile){
            var canvas = this.node.getComponent(cc.Canvas);
            canvas.fitHeight = true;
            canvas.fitWidth = true;
        }
        this.initMgr();

    },
    start:function(){        
        var self = this;
        var SHOW_TIME = 3000;
        var FADE_TIME = 500;
        /***
         * 
         * 控制登录界面或者广告首屏界面显示时间
         * 
         */
    },
    initMgr:function(){
        if(cc.beimi == null){
            /**
             * 增加了游戏全局变量控制，增加了 cc.beimi.gamestatus 参数，可选值：ready|notready|playing
             * @type {{}}
             */
            cc.beimi = {};
            cc.routes = {} ;
            cc.beimi.http = require("HTTP");
            cc.beimi.seckey = "beimi";
            cc.beimi.gamestatus = "none" ;

            cc.beimi.dialog = null ;

            cc.beimi.loadding = new cc.NodePool();
            cc.beimi.loadding.put(cc.instantiate(this.loaddingPrefab)); // 创建节点
            
            cc.beimi.dialog = new cc.NodePool();
            cc.beimi.dialog.put(cc.instantiate(this.alertPrefab)); // 创建节点
            
            if(cc.sys.isNative){
                window.io = SocketIO;
            }else{
                window.io = require("socket.io");
            }
        }
    }

});
