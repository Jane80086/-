// renderMode.ets
import { webview } from '@kit.ArkWeb';

// 跳转页面入口函数

@Builder
export function web2Builder(index:number) {
  web2();
}


@Component
struct web2 {

  webs2:string[]=[
  //江苏
    "http://www.jstc.org.cn/WebSite/104523/151807/showpage.shtml",
    //北京
    "https://www.woyaoce.cn/member/T104834/about.html",
    //天津
    "https://ketao.tten.cn/ShopManager/Institutionalprofile.aspx?Id=0c5446ae-97f0-4155-85bd-be4fa93a8368&show=1",


  ]
  pathStack: NavPathStack = new NavPathStack();
  private webviewController: WebviewController = new webview.WebviewController()
  private index:number=0
  build() {
    NavDestination() {
      Column() {
        Web({
          src:this.webs2[this.index],
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
