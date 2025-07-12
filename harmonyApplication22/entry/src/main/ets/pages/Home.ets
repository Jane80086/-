import image from "@ohos.multimedia.image"
import { it } from "@ohos/hypium"
import { AppStorageV2 } from "@kit.ArkUI"

interface FormClass{
  text:string
  icon:string
}
interface paramType {
  index:number;
}
@Component
export  struct Home {

  swiperList : string[]=[
    "https://sdstc.net/u/cms/www/202505/26162541to2c.png",
    "https://www.yuntest.top/uploads/20210809/1628489427909091914.png",
    "https://ts2.tc.mm.bing.net/th/id/OIP-C.eoRn0oXeCJPS6BtGZzt_XQAAAA?cb=thvnext&rs=1&pid=ImgDetMain&o=7&rm=3"

  ]
  forms:FormClass[]=[
    {text:'道普信息',icon:"https://image.qcc.com/logo/5c00f8ce6776a3a9c259e155deaaef97.jpg?x-oss-process=style/logo_s"},
  {text:'上海计算机',icon:"https://image.qcc.com/logo/ge02aaa28fa3cfb201ccaa29c040b56b.jpg?x-oss-process=style/logo_200"},
    {text:'北方实验室',icon:"https://qxb-logo-url.oss-cn-hangzhou.aliyuncs.com/OriginalUrl/2b917317fa263bc71db740bdde1eb648.jpg"},
    {text:'湖南佳策',icon:"https://zhengxin-pub.cdn.bcebos.com/logopic/25a324edab129d647252c86289738ad1_fullsize.jpg"}
  ]
  forms1:FormClass[]=[
    {text:'上海软评',icon:"https://zhengxin-pub.cdn.bcebos.com/logopic/89a913529d5c62933670f37ad2aa3bc0_fullsize.jpg?x-bce-process=image/resize,m_lfit,w_200"},
    {text:'吉林电检院',icon:"http://www.jldjy.org/images/dz_03.png"},
    {text:'重庆软评',icon:"http://www.cqstc.com/template/cqjb/pc/skin/images/logo.png?t=1650794426"},
    {text:'西安863',icon:"https://zhengxin-pub.cdn.bcebos.com/logopic/0e115a85c8101a2262e6df86631fafd8_fullsize.jpg?x-bce-process=image/resize,m_lfit,w_200"}
  ]
  forms2:FormClass[]=[
    {text:'江苏软检',icon:"https://ts4.tc.mm.bing.net/th/id/OIP-C.7peclaBzhMWpe2hNrMLp4QHaHa?cb=thvnext&rs=1&pid=ImgDetMain&o=7&rm=3"},
    {text:'北京软质检',icon:"https://ts1.tc.mm.bing.net/th/id/OIP-C.Fg9CiWM6utVXqxWf95dK-gHaHa?cb=thvnext&rs=1&pid=ImgDetMain&o=7&rm=3"},
    {text:'天津软评',icon:"https://zhengxin-pub.cdn.bcebos.com/logopic/01cf37b802127caada8d744c49cbf3b1_fullsize.jpg"},
    // {text:' ',icon:"https://zhengxin-pub.cdn.bcebos.com/logopic/25a324edab129d647252c86289738ad1_fullsize.jpg"}
  ]
  //跳转
  pathStack : NavPathStack =AppStorageV2.connect(NavPathStack,'navStack',()=>new NavPathStack())!



  build() {
    Column({space:10}){
      List(){
        //轮播
        ListItem(){
          //轮播图
          Swiper(){
            ForEach(this.swiperList,(item:string,index:number)=>{
              Image(item)
                .width('100%')
                .onClick(()=>{
                  this.pathStack.pushPathByName('meeting',null,false)
                })
            })
          }.autoPlay(true)
          .height('180')
          .margin({bottom:5})

        }.margin({bottom:5})
        //文本
        ListItem(){
          //文本框
          Column({space:5}) {
            Text('中国电子质量管理协会')
              .fontSize(20)
              .width('100%')
            Text('计算机软硬件和信息系统质量测评分会')
              .fontSize(16)
              .width('100%')
              .margin({top:5})

            Divider()
              .strokeWidth(2)
              .color('#ecf0f1')
              .width('100%')
              .margin({top:5,bottom:5})
            Text('      计算机软硬件和信息系统质量测评分会是中国电子质量管理协会设立的17个分支机构之一。分会可以对计算机软硬件和信息系统所有潜在的、现有的风险进行评估及分析并对自愿申请参加能力评审的造价评估机构进行客观、公正的能力评审，最终给出能力评审结论。')
              .fontSize(16)
              .textAlign(TextAlign.Start)
              .width('100%')
              .lineHeight('25')


          }

          .padding('25')
          .width('100%')
          .backgroundColor('#fff')
          .border({radius:15})
        }.margin({bottom:5})

        //企业列表
        ListItem(){
          Row(){
          ForEach(this.forms,(item:FormClass,index:number)=>{
            Column(){
              Image(item.icon)
                .margin({top:7,bottom:7})
                .width('40')
                .height('40')
              Text(item.text)
                .fontSize(14)}
            .onClick(()=>{
              // const params:paramType = { index :index};
              this.pathStack.pushPathByName('web0',index,false)
            })
            .layoutWeight(1)
            .height(90)
            .backgroundColor('#fff')
            .padding(7)
            .border({color:'#ecf0f1',width:1})
          })
        }}
        //企业列表1
        ListItem(){
          Row(){
            ForEach(this.forms1,(item:FormClass,index:number)=>{
              Column(){
                Image(item.icon)
                  .margin({top:7,bottom:7})
                  .width('40')
                  .height('40')
                Text(item.text)
                  .fontSize(14)}
              .onClick(()=>{
                // const params:paramType = { index :index};
                this.pathStack.pushPathByName('web',index,false)
              })
              .layoutWeight(1)
              .height(90)
              .backgroundColor('#fff')
              .padding(7)
              .border({color:'#ecf0f1',width:1})
            })
          }}
        //企业列表2
        ListItem(){
          Row(){
            ForEach(this.forms2,(item:FormClass,index:number)=>{
              Column(){
                Image(item.icon)
                  .margin({top:7,bottom:7})
                  .width('40')
                  .height('40')
                Text(item.text)
                  .fontSize(14)}
              .onClick(()=>{
                // const params:paramType = { index :index};
                this.pathStack.pushPathByName('web2',index,false)
              })
              .width(92)
              .height(90)
              .backgroundColor('#fff')
              .padding(7)
              .border({color:'#ecf0f1',width:1})
            })
          }}


      }.margin({top:3})
      .layoutWeight(1)
      .scrollBar(BarState.Off)
    }.width('100%')
    .height('100%')
    .padding({left:10,right:10,top:5,bottom:5})
  }
}