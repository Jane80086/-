import { AppStorageV2 } from "@kit.ArkUI"

@Component
export  struct Cooperation {
  //跳转
  pathStack : NavPathStack =AppStorageV2.connect(NavPathStack,'navStack',()=>new NavPathStack())!

  build() {
    Column({space:10}){
      Row({space:15}){
        Column(){
          Text('会  议  研  讨')
            .width(160)
            .height(80)
            .backgroundColor('#2655c5')
            .fontColor('#fff')
            .fontSize(20)
            .fontWeight('600',{enableVariableFontWeight:true})
            .padding({left:25})
          Text('协会组织会员单位召开行业会议，研讨相关技术与行业标准')
            .width(160)
            .height(80)
            .fontSize(14)
            .padding(6)
            .maxLines(2)
            .textOverflow({overflow:TextOverflow.Ellipsis})
        }
        .onClick(()=>{
          this.pathStack.pushPathByName('meeting',null,false)
        })
        .shadow({
          offsetX: 2,           // X轴偏移量
          offsetY: 4,           // Y轴偏移量
          radius: 15,            // 阴影半径（模糊效果）
          color: '#88000000'    // 阴影颜色，最后两位是透明度
        })
        .backgroundColor('#fff')
        .border({color:Color.Gray,width:2,radius:10})
        Column(){
          Text('标  准  制  定')
            .width(160)
            .height(80)
            .backgroundColor('#2655c5')
            .fontColor('#fff')
            .fontSize(20)
            .fontWeight('600',{enableVariableFontWeight:true})
            .padding({left:25})
          Text('协会组织成员单位联合制定软件测试行业标准')
            .width(160)
            .height(80)
            .fontSize(14)
            .padding(6)
            .maxLines(2)
            .textOverflow({overflow:TextOverflow.Ellipsis})
        }
        .shadow({
          offsetX: 2,           // X轴偏移量
          offsetY: 4,           // Y轴偏移量
          radius: 15,            // 阴影半径（模糊效果）
          color: '#88000000'    // 阴影颜色，最后两位是透明度
        })
        .backgroundColor('#fff')
        .border({color:Color.Gray,width:2,radius:10})
      }
      Row({space:15}){
        Column(){
          Text('技  术  培  训')
            .width(160)
            .height(80)
            .backgroundColor('#2655c5')
            .fontColor('#fff')
            .fontSize(20)
            .fontWeight('600',{enableVariableFontWeight:true})
            .padding({left:25})
          Text('协会组织会员单位开展软件测试技术、软件造价评估')
            .width(160)
            .height(80)
            .fontSize(14)
            .padding(6)
            .maxLines(2)
            .textOverflow({overflow:TextOverflow.Ellipsis})
        }
        .shadow({
          offsetX: 2,           // X轴偏移量
          offsetY: 4,           // Y轴偏移量
          radius: 15,            // 阴影半径（模糊效果）
          color: '#88000000'    // 阴影颜色，最后两位是透明度
        })
        .backgroundColor('#fff')
        .border({color:Color.Gray,width:2,radius:10})
        Column(){
          Text('工  具  研  发')
            .width(160)
            .height(80)
            .backgroundColor('#2655c5')
            .fontColor('#fff')
            .fontSize(20)
            .fontWeight('600',{enableVariableFontWeight:true})
            .padding({left:25})
          Text('协会组织会员单位共同研发软件测试相关工具。')
            .width(160)
            .height(80)
            .fontSize(14)
            .padding(6)
            .maxLines(2)
            .textOverflow({overflow:TextOverflow.Ellipsis})
        }
        .shadow({
          offsetX: 2,           // X轴偏移量
          offsetY: 4,           // Y轴偏移量
          radius: 15,            // 阴影半径（模糊效果）
          color: '#88000000'    // 阴影颜色，最后两位是透明度
        })
        .backgroundColor('#fff')
        .border({color:Color.Gray,width:2,radius:10})
      }
      Row({space:15}){
        Column(){
          Text('公  益  行  动')
            .width(160)
            .height(80)
            .backgroundColor('#ff6bb3')
            .fontColor('#fff')
            .fontSize(20)
            .fontWeight('600',{enableVariableFontWeight:true})
            .padding({left:25})
          Text('会员单位积极参与欠发达地区留守儿童公益活动。')
            .width(160)
            .height(80)
            .fontSize(14)
            .padding(6)
            .maxLines(2)
            .textOverflow({overflow:TextOverflow.Ellipsis})
        }   .shadow({
          offsetX: 2,           // X轴偏移量
          offsetY: 4,           // Y轴偏移量
          radius: 15,            // 阴影半径（模糊效果）
          color: '#88000000'    // 阴影颜色，最后两位是透明度
        })
        .backgroundColor('#fff')
        .border({color:'#88000000',width:2,radius:10})

      }.width('90%')
    }.width('100%')
    .height('100%')
    .padding({left:5,right:5,top:7,bottom:5})

  }
}