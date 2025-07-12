import { AppStorageV2 } from "@kit.ArkUI"
interface techClass{
  image:string
  title:string
  auther:string
}

@Component
export  struct Tech {
  @Builder classBuilder(image:string,title:string,auther:string){
    Column(){
      Text(image)
        .width('100%')
        .height(80)
        .backgroundColor('#2655c5')
        .fontColor('#fff')
        .fontSize(20)
        .fontWeight('600',{enableVariableFontWeight:true})
        .padding({left:25})
      Text(title)
        .width('100%')
        .height(80)
        .fontSize(14)
        .padding({left:5,right:5})
        .maxLines(2)
        .textOverflow({overflow:TextOverflow.Ellipsis})
    }
    .margin(3)
    .backgroundColor('#fff')
    .border({color:Color.Gray,width:2,radius:10})
    .onClick(()=>{
      this.pathStack.pushPathByName('TechDetail',null,false)
    })
  }

  techItems:techClass[]=[
    {image:'应用系统性能测试工具',title:'Leming WebRunner',auther:'迎风聚智'},
    {image:'数据库性能基准测试工具',title:'Leming TPC-E2.0',auther:'迎风聚智'},
    {image:'龙测自动化测试平台',title:'龙测AI-TestOps云平台',auther:'苏州市龙测智能科技有限公司'},
    {image:'软件造价评估系统',title:'软件造价评估系统',auther:'东北大学  郭军'},
    {image:'信安风评Cemenghui RiskHub',title:'信息安全风评系统',auther:'沈阳织知科技有限公司  武旭春'}
  ]
  @State searchKey:string=''
  @State filteredItems: techClass[] = this.techItems
  // 初始化时复制原始数据
  aboutToAppear() {
    this.filteredItems = [...this.techItems]
  }
  // 搜索逻辑更新函数
  updateSearchResults(keyword: string) {
    if (!keyword.trim()) {
      // 关键词为空时显示全部
      this.filteredItems = [...this.techItems]
    } else {
      // 根据关键词过滤
      this.filteredItems = this.techItems.filter(item =>
      item.title.includes(keyword) ||
      item.image.includes(keyword) ||
      item.auther.includes(keyword)
      )
    }
  }
  //跳转
  pathStack : NavPathStack =AppStorageV2.connect(NavPathStack,'navStack',()=>new NavPathStack())!
  build() {
    Column(){
      //搜索框
      Row(){
        Image($r('app.media.search'))
          .width('20')
          .fillColor(Color.Gray)
        TextInput({placeholder:'请搜索课程、工具或技术名称'}).onChange((value: string) => {
          // 文本内容发生变化时触发该回调
          this.searchKey=value
          console.info('onChange is triggering: ', value);
          this.updateSearchResults(value) // 更新搜索结果
        })
          .placeholderColor(Color.Gray)
          .backgroundColor('#FFF')
          .layoutWeight(1)
          .fontColor(Color.Black)
      }.width('100%')
      .height('40')
      .padding({left:9,right:9})
      .backgroundColor('#FFF')
      .border({radius:20})
      .margin({bottom:8})

      //列表
      List(){
        ForEach(this.filteredItems,(item:techClass)=>{
          if(item.title.includes(this.searchKey)){
          ListItem(){
            this.classBuilder(item.image,item.title,item.auther)
            }
          }

        })
      }










    }.width('100%')
    .height('100%')
    .padding({left:10,right:10,top:5,bottom:5})
  }
}