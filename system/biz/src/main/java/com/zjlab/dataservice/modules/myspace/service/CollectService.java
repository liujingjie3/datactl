package com.zjlab.dataservice.modules.myspace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.myspace.model.dto.collect.*;
import com.zjlab.dataservice.modules.myspace.model.po.CollectPo;
import com.zjlab.dataservice.modules.myspace.model.vo.CollectImageDetailVo;

public interface CollectService extends IService<CollectPo> {

    void collectMkdir(CollectMkdirDto mkdirDto);

    void collectMovePath(CollectMovePathDto movePathDto);
    void collectRenamePath(CollectRenamePathDto renamePathDto);

    PageResult<CollectImageDetailVo> qryCollectImageList(CollectImageListDto listDto);

//    CollectImageDetailVo qryCollectImageDetail(CollectImageDetailDto detailDto);

    void addCollectImage(CollectImageAddDto addDto);
//    void addCollectImageBatch(CollectImageAddBatchDto addBatchDto);

    void removeCollectImage(CollectImageRemoveDto removeDto);
    void removeCollectImageBatch(CollectImageRemoveBatchDto removeBatchDto);
}
