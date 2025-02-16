package com.p1.nomnom.store.entity;

import com.p1.nomnom.category.entity.Category;
import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "p_store")
@Getter
@Setter
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Many-to-One 관계로 User와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Many-to-One 관계로 User와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "phone", nullable = false, length = 255)
    private String phone;

    @Column(name = "open_time", nullable = false, length = 255)
    private String openTime;

    @Column(name = "close_time", nullable = false, length = 255)
    private String closeTime;

    @Column(name = "hidden", nullable = false)
    private Boolean hidden = false;

    // 가게 숨김 처리 (삭제 처리)
    public void hide(String deletedBy) {
        this.hidden = true;  // hidden을 true로 설정하여 숨김 처리
        this.markAsDeleted(deletedBy); // 삭제일 및 삭제자 설정
    }

    // 가게 복구 처리
    public void restoreStore(String updatedBy) {
        this.hidden = false;  // hidden을 false로 설정하여 복구
        this.unhide(updatedBy); // 삭제일 및 삭제자 초기화, 수정일 및 수정자 설정
    }
}
