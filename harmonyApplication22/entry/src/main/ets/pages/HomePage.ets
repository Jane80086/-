import { Cooperation } from "./Cooperation";
import { Home } from "./Home";
import { News } from "./News";
import { Tech } from "./Tech";
import { http } from '@kit.NetworkKit';
import { BusinessError } from '@kit.BasicServicesKit';
import { common } from '@kit.AbilityKit';
import { JSON } from '@kit.ArkTS';

interface TabClass{
  text:string
  icon:ResourceStr
}
// 跳转页面入口函数
@Builder
export function HomePageBuilder() {
  HomePage();
}


@ComponentV2
struct HomePage {

  @Local currentIndex:number=0
  pathStack: NavPathStack = new NavPathStack();
  tabDate:TabClass[]=[
    {text:'首页',icon:($r('app.media.shouye'))},
    {text:'动态',icon:($r('app.media.dongtai'))},
    {text:'技术',icon:($r('app.media.jishu'))},
    {text:'合作',icon:($r('app.media.hezuo'))},
  ]

@Builder tabBuilder(item:TabClass ,index:number){
    Column({space:5}){
      Image(item.icon)
        .width(24)
        .fillColor(this.currentIndex===index?'#e67e22':'#494a4b')//灰色
        .margin({top:8})
      Text(item.text)
        .fontColor(this.currentIndex===index?'#e67e22':'#494a4b')
    }
}

  build() {
    NavDestination() {
      Tabs({barPosition:BarPosition.End}){
        ForEach(this.tabDate,(item:TabClass,index:number)=>{
          TabContent(){
            if (index===0) {
              Home()
            }else if (index===1){
              News()
            }else if (index===2){
              Tech()
            }else {
              Cooperation()
            }
          }.tabBar(this.tabBuilder(item,index))
          .backgroundColor("#ecf0f1")
          .expandSafeArea([SafeAreaType.SYSTEM],[SafeAreaEdge.TOP,SafeAreaEdge.BOTTOM])
        })
      }.backgroundColor('#FFF')
      .onChange((index:number)=>{
        this.currentIndex=index
      })
       .expandSafeArea([SafeAreaType.SYSTEM],[SafeAreaEdge.TOP,SafeAreaEdge.BOTTOM])

    }
    .onReady((context: NavDestinationContext) => {

      this.pathStack = context.pathStack;
    })
  }
}