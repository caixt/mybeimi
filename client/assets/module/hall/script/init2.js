cc.Class({
    extends: cc.Component,

    properties: {
        // foo: {
        //    default: null,      // The default value will be used only when the component attaching
        //                           to a node for the first time
        //    url: cc.Texture2D,  // optional, default is typeof default
        //    serializable: true, // optional, default is true
        //    visible: true,      // optional, default is true
        //    displayName: 'Foo', // optional
        //    readonly: false,    // optional, default is false
        // },
        // ...
    },

    // use this for initialization
    onLoad: function () {
        cc.beimi = {}
        cc.beimi.user = {username : 'cxt', goldcoins : 12345678, cards : 4321};
        
        cc.beimi.games = [{"id":"402888815e0521d8015e052342080002","code":"dizhuhall","name":"斗地主","types":[{"id":"402888815e14abfb015e14b6e2080002","name":"经典玩法","code":"basic","playways":[{"id":"402888815e21d735015e21d995680000","name":"初级场","code":"dizhu","score":10000,"mincoins":1000,"maxcoins":100000,"changecard":false,"onlineusers":0,"shuffle":true,"level":"1","skin":"2"},{"id":"402888815e672639015e675125750000","name":"初级场（不洗牌）","code":"dizhu","score":10000,"mincoins":1000,"maxcoins":100000,"changecard":false,"onlineusers":0,"shuffle":false,"level":"1","skin":"1"},{"id":"402888815e671780015e6718ebce0000","name":"高级场","code":"dizhu","score":50000,"mincoins":50000,"maxcoins":200000,"changecard":false,"onlineusers":0,"shuffle":true,"level":"2","skin":"2"},{"id":"402888815e672639015e675215ed0001","name":"高级场（不洗牌）","code":"dizhu","score":50000,"mincoins":50000,"maxcoins":200000,"changecard":false,"onlineusers":0,"shuffle":false,"level":"2","skin":"2"}]},{"id":"402888815e14abfb015e14b6e21d0003","name":"房间模式","code":"room","playways":[]}]}];
        
        cc.beimi.game = {
            model : null ,
            playway : null,
            type:function(name){
                var temp ;
                if(cc.beimi.game.model !=null){
                    for(var i=0 ; i<cc.beimi.game.model.types.length ; i++){
                        var type = cc.beimi.game.model.types[i] ;
                        if(type.code == name){
                            temp = type ;
                        }
                    }
                }
                return temp ;
            }
        };
        
        cc.beimi.game.model = cc.beimi.games[0];
        
        
        
        var Audio = require("Audio");
        cc.beimi.audio = new Audio();
        cc.beimi.audio.init();
        cc.beimi.audio.playBGM("bgMain.mp3");
    },

    // called every frame, uncomment this function to activate update callback
    // update: function (dt) {

    // },
});
