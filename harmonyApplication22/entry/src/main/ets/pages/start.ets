// 跳转页面入口函数
@Builder
export function StartBuilder() {
  Start();
}


@Component
struct Start {
  pathStack: NavPathStack = new NavPathStack();
aboutToAppear(): void {
  //页面生命周期开始2s后自动跳转到首页
  setTimeout(()=>{
    this.pathStack.replacePathByName("HomePage", null, false);
  },2000)
}

  build() {
    NavDestination() {

      Stack({alignContent:Alignment.TopEnd}){
        Image($r('app.media.start_ic'))
          .width('100%')
          .height('100%')
          .expandSafeArea([SafeAreaType.SYSTEM],[SafeAreaEdge.TOP,SafeAreaEdge.BOTTOM])
        Button('跳过')
          .backgroundColor(Color.Gray)
          .margin(15)
          .onClick(()=>{
                this.pathStack.replacePathByName("HomePage", null, false);
              })
      }

    }
    // .title('Start')
    .onReady((context: NavDestinationContext) => {
      this.pathStack = context.pathStack;
    })
  }
}