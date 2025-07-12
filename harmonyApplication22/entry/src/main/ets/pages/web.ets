// renderMode.ets
import { webview } from '@kit.ArkWeb';

// 跳转页面入口函数

@Builder
export function webBuilder(index:number) {
  web();
}


@Component
struct web {

  webs1:string[]=[
    //上海软评
    "https://baike.baidu.com/item/%E4%B8%8A%E6%B5%B7%E5%B8%82%E8%BD%AF%E4%BB%B6%E8%AF%84%E6%B5%8B%E4%B8%AD%E5%BF%83/7957346",
    //吉林
    "http://www.jldjy.org/",
    //重庆
    "http://www.cqstc.com/",
    //西安
    "http://www.xa863.cn/",

  ]
  pathStack: NavPathStack = new NavPathStack();
  private webviewController: WebviewController = new webview.WebviewController()
private index:number=0
  build() {
    NavDestination() {
      Column() {
        Web({
          src:this.webs1[this.index],
          controller: this.webviewController,
          renderMode: RenderMode.ASYNC_RENDER // 设置渲染模式
        })
      }
    }
    .title('企业详情')
    .onReady((context: NavDestinationContext) => {
      this.pathStack = context.pathStack;
       // const params =Number (context.pathStack.getParamByName('index')!)// 假设有一个getParams的方法来获取参数
      this.index = context.pathInfo.param as number
    })
  }
}
