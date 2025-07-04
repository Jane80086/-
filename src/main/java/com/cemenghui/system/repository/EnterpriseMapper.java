package com.cemenghui.system.repository;

import com.cemenghui.common.Enterprise;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Repository
@Mapper
public interface EnterpriseMapper {

    /**
     * 根据企业ID查询企业工商信息
     * @param enterpriseId 企业ID
     * @return 企业工商信息实体
     */
    @Select("SELECT * FROM enterprise WHERE enterprise_id = #{enterpriseId}")
    Enterprise findByEnterpriseId(String enterpriseId);

    /**
     * 根据企业名称查询企业工商信息（用于注册时预填充 ）
     * @param enterpriseName 企业名称
     * @return 企业工商信息实体（包含统一社会信用代码、注册地址等 ）
     */
    @Select("SELECT * FROM enterprise WHERE enterprise_name = #{enterpriseName}")
    Enterprise findByEnterpriseName(String enterpriseName);

    /**
     * 保存企业工商信息（若需新增企业信息到数据库时使用 ）
     * @param info 企业工商信息实体
     */
    @Insert("INSERT INTO enterprise (" +
            "enterprise_id, enterprise_name, social_credit_code, register_address, legal_representative, " +
            "registration_date, enterprise_type, registered_capital, business_scope, establishment_date, " +
            "business_term, registration_authority, approval_date, enterprise_status, create_time, update_time" +
            ") VALUES (" +
            "#{enterpriseId}, #{enterpriseName}, #{socialCreditCode}, #{registerAddress}, #{legalRepresentative}, " +
            "#{registrationDate}, #{enterpriseType}, #{registeredCapital}, #{businessScope}, #{establishmentDate}, " +
            "#{businessTerm}, #{registrationAuthority}, #{approvalDate}, #{enterpriseStatus}, #{createTime}, #{updateTime}" +
            ")")
    void saveEnterprise(Enterprise info);

    /**
     * 统计同名企业数量（用于校验企业是否存在）
     */
    @Select("SELECT COUNT(*) FROM enterprise WHERE enterprise_name = #{enterpriseName}")
    int countByEnterpriseName(String enterpriseName);

    /**
     * 根据企业ID统计企业数量（用于校验企业是否存在）
     */
    @Select("SELECT COUNT(*) FROM enterprise WHERE enterprise_id = #{enterpriseId}")
    int countByEnterpriseId(String enterpriseId);

    @Insert("INSERT INTO enterprise (enterprise_id, enterprise_name, credit_code, legal_representative, registered_capital, establishment_date, register_address, business_scope, business_term, enterprise_status, enterprise_type, registration_authority, registration_date, approval_date, create_time, update_time) VALUES (#{enterpriseId}, #{enterpriseName}, #{creditCode}, #{legalRepresentative}, #{registeredCapital}, #{establishmentDate}, #{registerAddress}, #{businessScope}, #{businessTerm}, #{enterpriseStatus}, #{enterpriseType}, #{registrationAuthority}, #{registrationDate}, #{approvalDate}, #{createTime}, #{updateTime})")
    int insertEnterprise(Enterprise enterprise);

    @Select("SELECT * FROM enterprise LIMIT #{offset}, #{size}")
    List<Enterprise> selectEnterpriseList(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM enterprise")
    int countEnterprises();

    /**
     * 根据企业ID删除企业
     */
    @org.apache.ibatis.annotations.Delete("DELETE FROM enterprise WHERE enterprise_id = #{enterpriseId}")
    int deleteByEnterpriseId(@Param("enterpriseId") Long enterpriseId);

    /**
     * 更新企业信息
     */
    @org.apache.ibatis.annotations.Update("UPDATE enterprise SET enterprise_name = #{enterpriseName}, credit_code = #{creditCode}, legal_representative = #{legalRepresentative}, registered_capital = #{registeredCapital}, establishment_date = #{establishmentDate}, register_address = #{registerAddress}, business_scope = #{businessScope}, enterprise_status = #{enterpriseStatus}, update_time = NOW() WHERE enterprise_id = #{enterpriseId}")
    int updateEnterprise(Enterprise enterprise);
}
