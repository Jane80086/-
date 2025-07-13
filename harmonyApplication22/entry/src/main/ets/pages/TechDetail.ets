

// 跳转页面入口函数
@Builder
export function TechDetailBuilder() {
  TechDetail();
}


@Component
struct TechDetail {
  pathStack: NavPathStack = new NavPathStack();

   url:string = 'https://www.bilibili.com/video/BV1mdK6zZEr3?t=1054.9'


  build() {
    NavDestination() {

      Video({src:this.url})
        .height(200)
        .margin(5)
        .autoPlay(true)

      Text('技术详情页')
      Image($r('app.media.meeting2')).width(400)
    }
    .title('技术详情页')
    .onReady((context: NavDestinationContext) => {
      this.pathStack = context.pathStack;
    })
  }
}