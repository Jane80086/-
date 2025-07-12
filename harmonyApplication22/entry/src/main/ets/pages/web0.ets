// renderMode.ets
import { webview } from '@kit.ArkWeb';

// 跳转页面入口函数

@Builder
export function web0Builder(index:number) {
  web0();
}


@Component
struct web0 {

  webs0:string[]=[
  //道普信息
    "https://sdstc.net/",
    //上海计算机
    "https://www.sscenter.sh.cn/",
    //北方
    "http://www.northlab.cn/",
    //西安
    "http://www.jiacetest.com/",

  ]
  pathStack: NavPathStack = new NavPathStack();
  private webviewController: WebviewController = new webview.WebviewController()
  private index:number=0
  build() {
    NavDestination() {
      Column() {
        Web({
          src:this.webs0[this.index],
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
