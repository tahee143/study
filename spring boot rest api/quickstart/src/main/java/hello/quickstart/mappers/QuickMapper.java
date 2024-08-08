package hello.quickstart.mappers;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface QuickMapper {

    HashMap<String, Object> findById(HashMap<String, Object> paramMap);

    void resisterItem(HashMap<String, Object> paramMap);
}
