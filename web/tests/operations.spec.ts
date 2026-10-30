import {test,expect} from '@playwright/test';
test('integrated workspace, dynamic route, hover, workers and feedback',async({page})=>{test.setTimeout(90000);
 const errors:string[]=[];page.on('pageerror',e=>errors.push(e.message));await page.goto('/');await page.getByRole('button',{name:'登录工作台'}).click();await expect(page.getByRole('heading',{name:/工作总览/})).toBeVisible();
 await page.getByRole('link',{name:'管网与规划',exact:true}).click();
 await page.getByRole('button',{name:'计算巡检路线'}).click();await expect(page.locator('.route-result')).toContainText('124');
 // Preview must survive periodic refresh.
 await page.waitForTimeout(5500);await expect(page.getByRole('button',{name:'保存任务，进入审核'})).toBeEnabled();
 await page.getByPlaceholder('点击图上选择，或输入 J02, J05').fill('J02');await page.getByRole('button',{name:'计算巡检路线'}).click();await expect(page.locator('.route-result')).toContainText('127');
 const edge=page.locator('.network-edge').first();await edge.hover();await expect(page.getByRole('tooltip')).toContainText('管段档案');await edge.click();await expect(page.getByRole('tooltip')).toContainText('已选中');await page.getByRole('button',{name:'关闭管段信息'}).click();
 await page.screenshot({path:'test-results/v2-network.png',fullPage:true});
 await page.getByRole('link',{name:'工作人员',exact:true}).click();const before=await page.locator('.worker-card').count();
 await page.getByRole('button',{name:'新增工作人员'}).click();const modal=page.locator('.modal');const uname='testw'+Date.now();await modal.getByLabel('登录账号').fill(uname);await modal.getByLabel('姓名').fill('页面验证人员');await modal.getByLabel('负责片区').fill('西区');await modal.getByLabel('初始密码').fill('WorkerTest2026!');await modal.getByRole('button',{name:'保存工作人员'}).click();await expect(page.locator('.worker-card')).toHaveCount(before+1);
 const card=page.locator('.worker-card').filter({hasText:uname});await card.getByRole('button',{name:'编辑资料'}).click();await modal.getByLabel('启用账号').uncheck();await modal.getByRole('button',{name:'保存工作人员'}).click();await expect(card).toContainText('已停用');
 await page.getByRole('link',{name:'巡检任务',exact:true}).click();await page.locator('tbody tr').first().getByRole('button',{name:'详情',exact:true}).click();await expect(page.locator('.detail-drawer')).toContainText('作业时间线');await page.getByRole('button',{name:'关闭任务详情'}).click();
 await page.getByRole('link',{name:'管网与规划',exact:true}).click();await page.getByRole('button',{name:'公开管网参考'}).click();await expect(page.locator('.public-network h2')).toContainText('无压污水管网');await expect(page.locator('.public-canvas polyline')).toHaveCount(360,{timeout:25000});await page.screenshot({path:'test-results/v2-public-network.png',fullPage:true});await page.getByRole('button',{name:'关闭公开管网'}).click();
 await page.getByRole('button',{name:'导入 / 管理'}).click();page.once('dialog',d=>d.accept());await page.getByRole('button',{name:'载入园区支路示例'}).click();await expect(page.locator('.network-node')).toHaveCount(31);await page.locator('.segmented').getByRole('button',{name:'路线规划'}).click();await page.getByRole('button',{name:'计算巡检路线'}).click();await expect(page.locator('.route-result')).toContainText('124');await page.screenshot({path:'test-results/v2-expanded-network.png',fullPage:true});await page.setViewportSize({width:900,height:1000});await page.screenshot({path:'test-results/v2-tablet.png',fullPage:true});expect(errors).toEqual([]);
});
