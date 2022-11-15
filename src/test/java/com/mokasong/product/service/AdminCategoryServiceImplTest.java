package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.custom.ConflictException;
import com.mokasong.common.exception.custom.NotFoundException;
import com.mokasong.common.query.PageAndSearch;
import com.mokasong.product.dto.request.admin.CreateDetailCategoryRequest;
import com.mokasong.product.dto.request.admin.CreateRootCategoryRequest;
import com.mokasong.product.dto.request.admin.UpdateDetailCategoryRequest;
import com.mokasong.product.dto.request.admin.UpdateRootCategoryRequest;
import com.mokasong.product.dto.response.admin.AllCategoriesResponse;
import com.mokasong.product.dto.response.admin.DetailCategoriesResponse;
import com.mokasong.product.dto.response.admin.RootCategoriesResponse;
import com.mokasong.product.entity.Product;
import com.mokasong.product.entity.ProductDetailCategory;
import com.mokasong.product.entity.ProductRootCategory;
import com.mokasong.product.repository.AdminCategoryMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminCategoryServiceImplTest {
    @Mock
    AdminCategoryMapper adminCategoryMapper;
    AdminCategoryService service;

    @BeforeEach
    void init() {
        service = new AdminCategoryServiceImpl(adminCategoryMapper);
    }

    @Nested
    class createRootCategory {
        CreateRootCategoryRequest request = new CreateRootCategoryRequest();

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test() throws Exception {
                // given
                request.setName("테스트 이름");


                // stubbing
                doReturn(null)
                        .when(adminCategoryMapper).getRootCategoryByName(request.getName());


                // when
                SuccessfulCreateResponse response = service.createRootCategory(request);


                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("이름 중복")
            void duplicateName(@Mock ProductRootCategory rootCategory) {
                // given
                request.setName("테스트 이름");


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategoryByName(request.getName());


                // when & then
                Exception exception = assertThrows(ConflictException.class, () -> {
                    service.createRootCategory(request);
                });

                assertEquals("이름이 중복되는 카테고리가 존재합니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class createDetailCategory {
        CreateDetailCategoryRequest request = new CreateDetailCategoryRequest();

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock ProductRootCategory rootCategory) throws Exception {
                // given
                request.setName("테스트 이름");
                request.setRootCategoryId(1L);


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(request.getRootCategoryId());
                doReturn(null)
                        .when(adminCategoryMapper).getDetailCategoryByRootCategoryIdAndName(request.getRootCategoryId(), request.getName());


                // when
                SuccessfulCreateResponse response = service.createDetailCategory(request);


                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("최상위 카테고리 조회 안됨")
            void rootCategoryNotFound() {
                // given
                request.setName("테스트 이름");
                request.setRootCategoryId(1L);


                // stubbing
                doReturn(null)
                        .when(adminCategoryMapper).getRootCategory(request.getRootCategoryId());


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.createDetailCategory(request);
                });

                assertEquals("최상위 카테고리가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("이름 중복")
            void duplicateName(@Mock ProductRootCategory rootCategory, @Mock ProductDetailCategory sameNameDetailCategory) {
                // given
                request.setName("테스트 이름");
                request.setRootCategoryId(1L);


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(request.getRootCategoryId());
                doReturn(sameNameDetailCategory)
                        .when(adminCategoryMapper).getDetailCategoryByRootCategoryIdAndName(request.getRootCategoryId(), request.getName());


                // when & then
                Exception exception = assertThrows(ConflictException.class, () -> {
                    service.createDetailCategory(request);
                });

                assertEquals("같은 최상위 카테고리에 이름이 중복되는 상세 카테고리가 존재합니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class getRootCategories {
        PageAndSearch pageAndSearch = new PageAndSearch();

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test() throws Exception {
                // given
                pageAndSearch.setPage(1L);
                pageAndSearch.setLimit(10L);


                // stubbing
                doReturn(48L)
                        .when(adminCategoryMapper).getTotalCountOfRootCategories();
                doReturn(new ArrayList<RootCategoriesResponse.RootCategory>())
                        .when(adminCategoryMapper).getRootCategoriesByCondition(pageAndSearch.extractBegin(), pageAndSearch);


                // when
                RootCategoriesResponse response = service.getRootCategories(pageAndSearch);


                // then
                assertNotNull(response.getTotalCount());
                assertNotNull(response.getTotalPage());
                assertNotNull(response.getCurrentPage());
                assertNotNull(response.getCategories());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("유효하지 않은 페이지")
            void invalidPage() {
                // given
                pageAndSearch.setPage(6L);
                pageAndSearch.setLimit(10L);


                // stubbing
                doReturn(48L)
                        .when(adminCategoryMapper).getTotalCountOfRootCategories();


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.getRootCategories(pageAndSearch);
                });

                assertEquals("유효하지 않은 페이지입니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class getDetailCategories {
        PageAndSearch pageAndSearch = new PageAndSearch();

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test() throws Exception {
                // given
                pageAndSearch.setPage(1L);
                pageAndSearch.setLimit(10L);


                // stubbing
                doReturn(48L)
                        .when(adminCategoryMapper).getTotalCountOfDetailCategories();
                doReturn(new ArrayList<DetailCategoriesResponse.DetailCategory>())
                        .when(adminCategoryMapper).getDetailCategoriesByCondition(pageAndSearch.extractBegin(), pageAndSearch);


                // when
                DetailCategoriesResponse response = service.getDetailCategories(pageAndSearch);


                // then
                assertNotNull(response.getTotalCount());
                assertNotNull(response.getTotalPage());
                assertNotNull(response.getCurrentPage());
                assertNotNull(response.getCategories());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("유효하지 않은 페이지")
            void invalidPage() {
                // given
                pageAndSearch.setPage(6L);
                pageAndSearch.setLimit(10L);


                // stubbing
                doReturn(48L)
                        .when(adminCategoryMapper).getTotalCountOfDetailCategories();


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.getDetailCategories(pageAndSearch);
                });

                assertEquals("유효하지 않은 페이지입니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class getAllCategories {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test() throws Exception {
                // stubbing
                doReturn(new ArrayList<AllCategoriesResponse.RootCategory>())
                        .when(adminCategoryMapper).getAllCategories();


                // when
                AllCategoriesResponse response = service.getAllCategories();


                // then
                assertNotNull(response.getCategories());
            }
        }
    }

    @Nested
    class updateRootCategory {
        Long rootCategoryId;
        UpdateRootCategoryRequest request = new UpdateRootCategoryRequest();

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock ProductRootCategory rootCategory) throws Exception {
                // given
                rootCategoryId = 1L;
                request.setName("테스트 수정 이름");


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(false)
                        .when(rootCategory).same(request.getName());
                doReturn(null)
                        .when(adminCategoryMapper).getRootCategoryByName(request.getName());


                // when
                SuccessfulResponse response = service.updateRootCategory(rootCategoryId, request);


                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("조회 안됨")
            void rootCategoryNotFound() throws Exception {
                // given
                request.setName("테스트 수정 이름");
                rootCategoryId = 1L;


                // stubbing
                doReturn(null)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.updateRootCategory(rootCategoryId, request);
                });

                assertEquals("최상위 카테고리가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("이미 설정한 이름")
            void alreadyThatName(@Mock ProductRootCategory rootCategory) throws Exception {
                // given
                request.setName("테스트 이름");
                rootCategoryId = 1L;


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(true)
                        .when(rootCategory).same(request.getName());


                // when & then
                Exception exception = assertThrows(ConflictException.class, () -> {
                    service.updateRootCategory(rootCategoryId, request);
                });

                assertEquals("이미 해당 이름으로 설정되어 있습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("이름 중복")
            void duplicateName(@Mock ProductRootCategory rootCategory, @Mock ProductRootCategory sameNameRootCategory) throws Exception {
                // given
                request.setName("테스트 수정 이름");
                rootCategoryId = 1L;


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(false)
                        .when(rootCategory).same(request.getName());
                doReturn(sameNameRootCategory)
                        .when(adminCategoryMapper).getRootCategoryByName(request.getName());
                doReturn(false)
                        .when(sameNameRootCategory).sameEntity(rootCategory);


                // when & then
                Exception exception = assertThrows(ConflictException.class, () -> {
                    service.updateRootCategory(rootCategoryId, request);
                });

                assertEquals("이름이 중복되는 카테고리가 존재합니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class updateDetailCategory {
        Long rootCategoryId, detailCategoryId;
        UpdateDetailCategoryRequest request = new UpdateDetailCategoryRequest();

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock ProductRootCategory rootCategory, @Mock ProductDetailCategory detailCategory,
                      @Mock ProductRootCategory newRootCategory) throws Exception {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;
                request.setName("테스트 수정 이름");
                request.setNewRootCategoryId(2L);


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);
                doReturn(true)
                        .when(detailCategory).same(rootCategoryId);
                doReturn(false)
                        .when(detailCategory).same(request.getName());
                doReturn(newRootCategory)
                        .when(adminCategoryMapper).getRootCategory(request.getNewRootCategoryId());
                doReturn(null)
                        .when(adminCategoryMapper).getDetailCategoryByRootCategoryIdAndName(request.getNewRootCategoryId(), request.getName());


                // when
                SuccessfulResponse response = service.updateDetailCategory(rootCategoryId, detailCategoryId, request);


                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("최상위 카테고리 조회 안됨")
            void rootCategoryNotFound() throws Exception {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;
                request.setName("테스트 수정 이름");
                request.setNewRootCategoryId(2L);


                // stubbing
                doReturn(null)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.updateDetailCategory(rootCategoryId, detailCategoryId, request);
                });

                assertEquals("최상위 카테고리가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("상세 카테고리 조회 안됨")
            void detailCategoryNotFound(@Mock ProductRootCategory rootCategory) throws Exception {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;
                request.setName("테스트 수정 이름");
                request.setNewRootCategoryId(2L);


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(null)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.updateDetailCategory(rootCategoryId, detailCategoryId, request);
                });

                assertEquals("상세 카테고리가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("상세 카테고리가 최상위 카테고리 종류가 아님")
            void detailNotKindOfRoot(@Mock ProductRootCategory rootCategory, @Mock ProductDetailCategory detailCategory) throws Exception {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;
                request.setName("테스트 수정 이름");
                request.setNewRootCategoryId(2L);


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);
                doReturn(false)
                        .when(detailCategory).same(rootCategoryId);


                // when & then
                Exception exception = assertThrows(ConflictException.class, () -> {
                    service.updateDetailCategory(rootCategoryId, detailCategoryId, request);
                });

                assertEquals("상세 카테고리가 최상위 카테고리의 종류가 아닙니다.", exception.getMessage());
            }

            @Test
            @DisplayName("수정된 내용 없음")
            void sameAsBefore(@Mock ProductRootCategory rootCategory, @Mock ProductDetailCategory detailCategory) throws Exception {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;
                request.setName("테스트 이름");
                request.setNewRootCategoryId(1L);


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);
                doReturn(true)
                        .when(detailCategory).same(rootCategoryId);
                doReturn(true)
                        .when(detailCategory).same(request.getName());


                // when & then
                Exception exception = assertThrows(ConflictException.class, () -> {
                    service.updateDetailCategory(rootCategoryId, detailCategoryId, request);
                });

                assertEquals("수정된 내용이 없습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("새 최상위 카테고리가 조회 안됨")
            void newRootCategoryNotFound(@Mock ProductRootCategory rootCategory, @Mock ProductDetailCategory detailCategory) throws Exception {
                // given
                request.setName("테스트 이름");
                request.setNewRootCategoryId(2L);
                rootCategoryId = 1L;
                detailCategoryId = 1L;


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);
                doReturn(true)
                        .when(detailCategory).same(rootCategoryId);
                doReturn(false)
                        .when(detailCategory).same(request.getName());
                doReturn(null)
                        .when(adminCategoryMapper).getRootCategory(request.getNewRootCategoryId());


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.updateDetailCategory(rootCategoryId, detailCategoryId, request);
                });

                assertEquals("새로 요청한 최상위 카테고리가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("이름 중복")
            void sameNameAsBefore(@Mock ProductRootCategory rootCategory, @Mock ProductRootCategory newRootCategory,
                                  @Mock ProductDetailCategory detailCategory, @Mock ProductDetailCategory sameNameDetailCategory) throws Exception {
                // given
                request.setName("테스트 이름");
                request.setNewRootCategoryId(2L);
                rootCategoryId = 1L;
                detailCategoryId = 1L;


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);
                doReturn(true)
                        .when(detailCategory).same(rootCategoryId);
                doReturn(false)
                        .when(detailCategory).same(request.getName());
                doReturn(newRootCategory)
                        .when(adminCategoryMapper).getRootCategory(request.getNewRootCategoryId());
                doReturn(sameNameDetailCategory)
                        .when(adminCategoryMapper).getDetailCategoryByRootCategoryIdAndName(request.getNewRootCategoryId(), request.getName());
                doReturn(false)
                        .when(sameNameDetailCategory).sameEntity(detailCategory);


                // when & then
                Exception exception = assertThrows(ConflictException.class, () -> {
                    service.updateDetailCategory(rootCategoryId, detailCategoryId, request);
                });

                assertEquals("같은 최상위 카테고리에 이름이 중복되는 상세 카테고리가 존재합니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class deleteRootCategory {
        Long rootCategoryId;

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock ProductRootCategory rootCategory) throws Exception {
                // given
                rootCategoryId = 1L;


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(new ArrayList<ProductDetailCategory>())
                        .when(adminCategoryMapper).getDetailCategoriesIncludedInRootCategory(rootCategoryId);


                // when
                SuccessfulResponse response = service.deleteRootCategory(rootCategoryId);


                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("최상위 카테고리 조회 안됨")
            void rootCategoryNotFound() {
                // given
                rootCategoryId = 1L;


                // stubbing
                doReturn(null)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.deleteRootCategory(rootCategoryId);
                });

                assertEquals("최상위 카테고리가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("사용하는 상세 카테고리가 존재")
            void detailCategoryExist(@Mock ProductRootCategory rootCategory) {
                // given
                rootCategoryId = 1L;


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);

                List<ProductDetailCategory> detailCategories = new ArrayList<>();
                detailCategories.add(new ProductDetailCategory());

                doReturn(detailCategories)
                        .when(adminCategoryMapper).getDetailCategoriesIncludedInRootCategory(rootCategoryId);


                // when & then
                Exception exception = assertThrows(ConflictException.class, () -> {
                    service.deleteRootCategory(rootCategoryId);
                });

                assertEquals("해당 최상위 카테고리를 사용하는 상세 카테고리가 존재하기 때문에 삭제할 수 없습니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class deleteDetailCategory {
        Long rootCategoryId, detailCategoryId;

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock ProductRootCategory rootCategory, @Mock ProductDetailCategory detailCategory) throws Exception {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);
                doReturn(true)
                        .when(detailCategory).same(rootCategoryId);
                doReturn(new ArrayList<Product>())
                        .when(adminCategoryMapper).getProductsIncludedInDetailCategory(detailCategoryId);


                // when
                SuccessfulResponse response = service.deleteDetailCategory(rootCategoryId, detailCategoryId);


                // then
                assertTrue(response.getSuccess());
            }
        }


        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("최상위 카테고리 조회 안됨")
            void rootCategoryNotExist() {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;


                // stubbing
                doReturn(null)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.deleteDetailCategory(rootCategoryId, detailCategoryId);
                });

                assertEquals("최상위 카테고리가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("상세 카테고리 조회 안됨")
            void detailCategoryNotExist(@Mock ProductRootCategory rootCategory) {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(null)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.deleteDetailCategory(rootCategoryId, detailCategoryId);
                });

                assertEquals("상세 카테고리가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("상세 카테고리가 최상위 카테고리 종류가 아님")
            void detailNotKindOfRoot(@Mock ProductRootCategory rootCategory, @Mock ProductDetailCategory detailCategory) {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);
                doReturn(false)
                        .when(detailCategory).same(rootCategoryId);


                // when & then
                Exception exception = assertThrows(ConflictException.class, () -> {
                    service.deleteDetailCategory(rootCategoryId, detailCategoryId);
                });

                assertEquals("상세 카테고리가 최상위 카테고리의 종류가 아닙니다.", exception.getMessage());
            }

            @Test
            @DisplayName("사용하는 상품이 존재")
            void productUsingDetailExist(@Mock ProductRootCategory rootCategory, @Mock ProductDetailCategory detailCategory) {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;


                // stubbing
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);
                doReturn(true)
                        .when(detailCategory).same(rootCategoryId);

                List<Product> products = new ArrayList<>();
                products.add(new Product());

                doReturn(products)
                        .when(adminCategoryMapper).getProductsIncludedInDetailCategory(detailCategoryId);


                // when & then
                Exception exception = assertThrows(ConflictException.class, () -> {
                    service.deleteDetailCategory(rootCategoryId, detailCategoryId);
                });

                assertEquals("해당 카테고리를 사용하는 상품이 있기 때문에 삭제할 수 없습니다.", exception.getMessage());
            }
        }
    }
}