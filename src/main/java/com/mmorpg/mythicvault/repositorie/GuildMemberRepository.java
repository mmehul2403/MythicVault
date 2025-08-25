package com.mmorpg.mythicvault.repositorie;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mmorpg.mythicvault.entity.GuildMember;
import com.mmorpg.mythicvault.entity.GuildMemberId;

@Repository
public interface GuildMemberRepository extends JpaRepository<GuildMember, GuildMemberId> {

	@EntityGraph(attributePaths = { "character", "role" })
	List<GuildMember> findByGuildId(Long guildId);

	@Query("""
			select gm from GuildMember gm
			join gm.role gr
			where gm.guild.id = :guildId and gr.code = :roleCode
			""")
	List<GuildMember> findByGuildAndRole(@Param("guildId") Long guildId, @Param("roleCode") String roleCode);
}