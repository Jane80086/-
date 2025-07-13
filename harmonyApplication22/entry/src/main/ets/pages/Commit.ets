// 跳转页面入口函数
@Builder
export function CommitBuilder() {
  Commit();
}


@Component
struct Commit {
  pathStack: NavPathStack = new NavPathStack();
  items:string[]=[
    '单位',
    '姓名',
    '性别',
    '手机号码',
    '电子邮箱',
    '到达方式',
    '到达车次',
    '到达时间',
    '备注',

  ]
  build() {
    NavDestination() {
      Column({space:5}){
        ForEach(this.items,(item:string)=>{
          Row(){
            Text(item+':')
            TextInput({placeholder:'请输入……'})
              .placeholderColor(Color.Gray)
              .backgroundColor('#FFF')
              .layoutWeight(1)
              .fontColor(Color.Black)
          }.width('100%')
          .height('40')
          .padding({left:9,right:9})
          .backgroundColor('#FFF')
          .border({radius:20})
        })

      }
      Button('保存并提交').width('100%').onClick(()=>{
        this.pathStack.pop('meeting',null)
      })

    }
    .title('参会回执')
    .onReady((context: NavDestinationContext) => {
      this.pathStack = context.pathStack;
    })
  }
}