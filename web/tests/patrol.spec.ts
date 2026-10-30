import {test,expect} from '@playwright/test';
test('management plans, approves, dispatches, messages and exports a task',async({page})=>{
 const taskName='流程验证-'+Date.now();const errors:string[]=[];page.on('pageerror',e=>errors.push(e.message));
 await page.goto('/');await expect(page.getByRole('heading',{name:'进入巡检工作台'})).toBeVisible();
 await page.getByRole('button',{name:'登录工作台'}).click();await expect(page.getByRole('heading',{name:/工作总览/})).toBeVisible();
 await page.screenshot({path:'test-results/overview.png',fullPage:true});
 await page.getByRole('link',{name:'管网与规划',exact:true}).click();
 await page.locator('.planning-panel').getByLabel('任务名称').fill(taskName);await page.getByRole('button',{name:'计算巡检路线'}).click();await expect(page.locator('.route-result')).toContainText('124');
 await page.getByRole('button',{name:'保存任务，进入审核'}).click();await expect(page.locator('.notice')).toContainText('任务已保存');
 await page.getByRole('link',{name:'巡检任务',exact:true}).click();
 const row=page.locator('tbody tr').filter({hasText:taskName}).first();await row.getByRole('button',{name:'审核通过'}).click();await row.getByRole('button',{name:'下发任务'}).click();await expect(row).toContainText('待进场');
 await page.getByPlaceholder('输入现场提示或任务补充说明').fill('前往起点检查井，确认回收条件');await page.getByRole('button',{name:'发送',exact:true}).click();await expect(page.locator('.message-history')).toContainText('确认回收条件');
 const download=page.waitForEvent('download');await row.getByTitle('导出任务资料').click();expect((await download).suggestedFilename()).toMatch(/T-.*json/);
 await page.setViewportSize({width:800,height:1000});await page.screenshot({path:'test-results/tablet.png',fullPage:true});expect(errors).toEqual([]);
});
