// menu.js
import Menu from '../../../../../../../tnxweb/components/tnxcore-menu';

export default new Menu({
    url: '/manager/self/authorities',
    items: [{
        caption: '系统管理',
        icon: 'fa-cogs',
        subs: [{
            caption: '管理员管理',
            icon: 'fa-users-cog',
            path: '/manager/list',
            rank: 'top',
            subs: [{
                caption: '新增管理员',
                path: '/manager/add',
                rank: 'top',
            }, {
                caption: '修改管理员',
                path: '/manager/:id/update',
                rank: 'top',
            }, {
                caption: '禁用/启用管理员',
                path: '/manager/:id/disable',
                rank: 'top',
            }],
        }, {
            caption: '角色管理',
            icon: 'fa-id-badge',
            path: '/role/list',
            rank: 'top',
            subs: [{
                caption: '新增角色',
                path: '/role/add',
                rank: 'top',
            }, {
                caption: '修改角色',
                path: '/role/:id/update',
                rank: 'top',
            }, {
                caption: '删除角色',
                path: '/role/:id/delete',
                rank: 'top',
            }],
        }, {
            caption: '组织架构',
            icon: 'fa-sitemap',
            path: '/dept/list',
            rank: 'top',
            subs: [{
                caption: '新增部门',
                path: '/dept/add',
                rank: 'top',
            }, {
                caption: '修改部门',
                path: '/dept/:id/update',
                rank: 'top',
            }, {
                caption: '删除部门',
                path: '/dept/:id/delete',
                rank: 'top',
            }, {
                caption: '新增岗位',
                path: '/job-position/add',
                rank: 'top',
            }, {
                caption: '修改岗位',
                path: '/job-position/:id/update',
                rank: 'top',
            }, {
                caption: '删除岗位',
                path: '/job-position/:id/delete',
                rank: 'top',
            }],
        }]
    }, {
        caption: '客户管理',
        desc: '查询客户清单',
        icon: 'fa-user',
        path: '/customer/list',
        permission: 'CUSTOMER_LIST',
        subs: [{
            caption: '查看客户详情',
            path: '/customer/:id/detail',
            permission: 'CUSTOMER_DETAIL'
        }, {
            caption: '禁用/启用客户',
            path: '/customer/:id/disable',
            permission: 'CUSTOMER_DISABLE'
        }]
    }]
});
