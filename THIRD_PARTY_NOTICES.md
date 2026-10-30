# 第三方数据与依赖

本仓库原创软件代码采用 MIT 许可证。第三方数据、地图服务、依赖库及开发工具遵循其各自许可证，MIT 不替代这些许可。

## 黄金海岸市无压污水管网

- 提供方及署名：**City of Gold Coast**。
- 数据名称：**Sewer Pipe Non Pressure**。
- 本仓库文件：`web/public/reference/gold-coast-sewer.geojson`。
- 原始图层：[ArcGIS FeatureServer / Layer 1](https://services.arcgis.com/3vStCH7NDoBOZ5zn/arcgis/rest/services/Sewer_Pipe_Non_Pressure/FeatureServer/1)。
- 发布条目：[ArcGIS 数据许可和元数据](https://www.arcgis.com/home/item.html?id=ffa11b9070484df3b5bef5d1e4592a8e)。
- 许可：**Creative Commons Attribution 3.0（CC BY 3.0）**；发布方元数据提供 [3.0 许可](https://creativecommons.org/licenses/by/3.0/) 及 [Australia 3.0 链接](https://creativecommons.org/licenses/by/3.0/au/)，使用时请保留发布方署名并以原始条目中的许可条件为准。
- 快照采集日期：2026-10-29；本次元数据核对日期：2026-10-30。
- 本仓库处理方式：按 Surfers Paradise 附近矩形范围查询，输出 WGS84 GeoJSON，选择部分字段，最多保留 180 条记录；属于原数据的局部提取及格式转换。

该文件是公开管网参考样本，不属于本项目现场巡检成果。界面使用的园区示例拓扑和本文件分别管理。源数据查询范围及字段见 [数据来源说明](docs/真实管网数据来源.txt)。

## OpenStreetMap

网页可选择加载 OpenStreetMap 地面底图，保留“© OpenStreetMap contributors”署名。底图在线加载，不随本仓库提供离线瓦片。

- [数据版权与 ODbL 说明](https://www.openstreetmap.org/copyright)
- [标准瓦片服务使用政策](https://operations.osmfoundation.org/policies/tiles/)

## 软件依赖

本仓库通过 Maven、npm 和 DevEco 工具引用依赖，不重新许可其原始代码或 SDK。依赖版本见 `backend/pom.xml`、`web/package-lock.json` 和 `harmony-app/oh-package.json5`，各依赖的完整许可请查阅其发布包及上游项目。
