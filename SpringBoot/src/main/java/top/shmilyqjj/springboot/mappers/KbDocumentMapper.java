package top.shmilyqjj.springboot.mappers;

import org.apache.ibatis.annotations.Mapper;
import top.shmilyqjj.springboot.models.entity.KbDocument;

import java.util.List;

@Mapper
public interface KbDocumentMapper {

    int insert(KbDocument doc);

    int deleteById(Long id);

    KbDocument findById(Long id);

    List<KbDocument> listAll();

    long countAll();
}
