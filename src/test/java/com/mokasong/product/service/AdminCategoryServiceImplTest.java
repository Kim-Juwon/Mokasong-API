package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.custom.ConflictException;
import com.mokasong.common.exception.custom.InternalServerErrorException;
import com.mokasong.common.exception.custom.NotFoundException;
import com.mokasong.common.exception.custom.PreconditionFailedException;
import com.mokasong.common.query.PageAndSearch;
import com.mokasong.product.dto.request.CreateDetailCategoryRequest;
import com.mokasong.product.dto.request.CreateRootCategoryRequest;
import com.mokasong.product.dto.request.UpdateDetailCategoryRequest;
import com.mokasong.product.dto.request.UpdateRootCategoryRequest;
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

    @Nested
    class CreateRootCategory {
        CreateRootCategoryRequest request = new CreateRootCategoryRequest();

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            void test() throws Exception {
                // given
                request.setName("테스트 이름");

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(null)
                        .when(adminCategoryMapper).getRootCategoryByName(request.getName());

                // when
                SuccessfulCreateResponse response = service.createRootCategory(request);

                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Fail {

            @Test
            @DisplayName("이름 중복")
            void duplicateName() {
                // given
                request.setName("테스트 이름");

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategoryByName(request.getName());

                // when & then
                assertThrows(ConflictException.class, () -> {
                    service.createRootCategory(request);
                });
            }
        }
    }

    @Nested
    class createDetailCategory {
        CreateDetailCategoryRequest request = new CreateDetailCategoryRequest();

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            void test() throws Exception {
                // given
                request.setName("테스트 이름");
                request.setRootCategoryId(1L);

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(request.getRootCategoryId());
                doReturn(null)
                        .when(adminCategoryMapper).getDetailCategoryByName(request.getName());

                // when
                SuccessfulCreateResponse response = service.createDetailCategory(request);

                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Fail {

            @Test
            @DisplayName("최상위 카테고리 미존재")
            void rootCategoryNotFound() {
                // given
                request.setName("테스트 이름");
                request.setRootCategoryId(1L);

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(null)
                        .when(adminCategoryMapper).getRootCategory(request.getRootCategoryId());

                // when & then
                assertThrows(InternalServerErrorException.class, () -> {
                    service.createDetailCategory(request);
                });
            }

            @Test
            @DisplayName("이름 중복")
            void duplicateName(@Mock ProductDetailCategory detailCategory) {
                // given
                request.setName("테스트 이름");
                request.setRootCategoryId(1L);

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(request.getRootCategoryId());
                doReturn(true)
                        .when(detailCategory).isKindOf(request.getRootCategoryId());
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategoryByName(request.getName());

                // when & then
                assertThrows(ConflictException.class, () -> {
                    service.createDetailCategory(request);
                });
            }
        }
    }

    @Nested
    class getRootCategories {
        PageAndSearch queryParameters = new PageAndSearch();

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            void test() throws Exception {
                // given
                queryParameters.setPage(1L);
                queryParameters.setLimit(10L);

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(20L)
                        .when(adminCategoryMapper).getTotalCountOfRootCategories();
                doReturn(new ArrayList<RootCategoriesResponse.RootCategory>())
                        .when(adminCategoryMapper).getRootCategoriesByCondition(queryParameters.generateBegin(), queryParameters);

                // when
                RootCategoriesResponse response = service.getRootCategories(queryParameters);

                // then
                assertNotNull(response.getTotalCount());
                assertNotNull(response.getTotalPage());
                assertNotNull(response.getCurrentPage());
                assertNotNull(response.getCategories());
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Fail {

            @Test
            @DisplayName("유효하지 않은 페이지")
            void pageInvalid() {
                // given
                queryParameters.setPage(3L);
                queryParameters.setLimit(10L);

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(10L)
                        .when(adminCategoryMapper).getTotalCountOfRootCategories();

                // when & then
                assertThrows(NotFoundException.class, () -> {
                    service.getRootCategories(queryParameters);
                });
            }
        }
    }

    @Nested
    class getDetailCategories {
        PageAndSearch queryParameters = new PageAndSearch();

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            void test() throws Exception {
                // given
                queryParameters.setPage(1L);
                queryParameters.setLimit(10L);

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(20L)
                        .when(adminCategoryMapper).getTotalCountOfDetailCategories();
                doReturn(new ArrayList<DetailCategoriesResponse.DetailCategory>())
                        .when(adminCategoryMapper).getDetailCategoriesByCondition(queryParameters.generateBegin(), queryParameters);

                // when
                DetailCategoriesResponse response = service.getDetailCategories(queryParameters);

                // then
                assertNotNull(response.getTotalCount());
                assertNotNull(response.getTotalPage());
                assertNotNull(response.getCurrentPage());
                assertNotNull(response.getCategories());
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Fail {

            @Test
            @DisplayName("유효하지 않은 페이지")
            void pageInvalid() {
                // given
                queryParameters.setPage(3L);
                queryParameters.setLimit(10L);

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(10L)
                        .when(adminCategoryMapper).getTotalCountOfDetailCategories();

                // when & then
                assertThrows(NotFoundException.class, () -> {
                    service.getDetailCategories(queryParameters);
                });
            }
        }
    }

    @Nested
    class getAllCategories {

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            void test() throws Exception {
                // given
                service = new AdminCategoryServiceImpl(adminCategoryMapper);

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
        UpdateRootCategoryRequest request = new UpdateRootCategoryRequest();
        Long rootCategoryId;

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            void test(@Mock ProductRootCategory rootCategory) throws Exception {
                // given
                request.setName("테스트 이름");
                rootCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(false)
                        .when(rootCategory).isSame(request.getName());
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(null)
                        .when(adminCategoryMapper).getRootCategoryByName(request.getName());

                // when
                SuccessfulResponse response = service.updateRootCategory(rootCategoryId, request);

                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Fail {

            @Test
            @DisplayName("카테고리 미존재")
            void notFound() throws Exception {
                // given
                request.setName("테스트 이름");
                rootCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(null)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);

                // when & then
                assertThrows(NotFoundException.class, () -> {
                    service.updateRootCategory(rootCategoryId, request);
                });
            }

            @Test
            @DisplayName("이미 설정한 이름")
            void alreadyThatName(@Mock ProductRootCategory rootCategory) throws Exception {
                // given
                request.setName("테스트 이름");
                rootCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(true)
                        .when(rootCategory).isSame(request.getName());
                doReturn(rootCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);

                // when & then
                assertThrows(ConflictException.class, () -> {
                    service.updateRootCategory(rootCategoryId, request);
                });
            }

            @Test
            @DisplayName("이름 중복")
            void duplicateName(@Mock ProductRootCategory existingCategory,
                               @Mock ProductRootCategory sameNameCategory) throws Exception {
                // given
                request.setName("테스트 이름");
                rootCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(false)
                        .when(existingCategory).isSame(request.getName());
                doReturn(existingCategory)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(false)
                        .when(sameNameCategory).isSame(rootCategoryId);
                doReturn(sameNameCategory)
                        .when(adminCategoryMapper).getRootCategoryByName(request.getName());

                // when & then
                assertThrows(ConflictException.class, () -> {
                    service.updateRootCategory(rootCategoryId, request);
                });
            }
        }
    }

    @Nested
    class updateDetailCategory {
        UpdateDetailCategoryRequest request = new UpdateDetailCategoryRequest();
        Long rootCategoryId, detailCategoryId;

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            void test(@Mock ProductDetailCategory detailCategory) throws Exception {
                // given
                request.setName("테스트 이름");
                request.setNewRootCategoryId(2L);
                rootCategoryId = 1L;
                detailCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(true)
                        .when(detailCategory).isKindOf(rootCategoryId);
                doReturn(false)
                        .when(detailCategory).isSame(request.getName());
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);
                doReturn(null)
                        .when(adminCategoryMapper).getDetailCategoryByName(request.getName());

                // when
                SuccessfulResponse response = service.updateDetailCategory(rootCategoryId, detailCategoryId, request);

                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Fail {

            @Test
            @DisplayName("최상위 카테고리 미존재")
            void RootCategoryNotFound() throws Exception {
                // given
                request.setName("테스트 이름");
                request.setNewRootCategoryId(2L);
                rootCategoryId = 1L;
                detailCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(null)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);

                // when & then
                assertThrows(NotFoundException.class, () -> {
                    service.updateDetailCategory(rootCategoryId, detailCategoryId, request);
                });
            }

            @Test
            @DisplayName("상세 카테고리 미존재")
            void detailCategoryNotFound() throws Exception {
                // given
                request.setName("테스트 이름");
                request.setNewRootCategoryId(2L);
                rootCategoryId = 1L;
                detailCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(null)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);

                // when & then
                assertThrows(NotFoundException.class, () -> {
                    service.updateDetailCategory(rootCategoryId, detailCategoryId, request);
                });
            }

            @Test
            @DisplayName("최상위 카테고리 소속이 아님")
            void detailNotKindOfRoot(@Mock ProductDetailCategory detailCategory) throws Exception {
                // given
                request.setName("테스트 이름");
                request.setNewRootCategoryId(2L);
                rootCategoryId = 1L;
                detailCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(false)
                        .when(detailCategory).isKindOf(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);

                // when & then
                assertThrows(PreconditionFailedException.class, () -> {
                    service.updateDetailCategory(rootCategoryId, detailCategoryId, request);
                });
            }

            @Test
            @DisplayName("최상위 카테고리 소속이 아님")
            void sameAsBefore(@Mock ProductDetailCategory detailCategory) throws Exception {
                // given
                request.setName("테스트 이름");
                request.setNewRootCategoryId(2L);
                rootCategoryId = 1L;
                detailCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(true)
                        .when(detailCategory).isKindOf(rootCategoryId);
                doReturn(true)
                        .when(detailCategory).isSame(request.getName());
                doReturn(true)
                        .when(detailCategory).isKindOf(request.getNewRootCategoryId());
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);

                // when & then
                assertThrows(ConflictException.class, () -> {
                    service.updateDetailCategory(rootCategoryId, detailCategoryId, request);
                });
            }

            @Test
            @DisplayName("이름 중복")
            void sameAsBefore(@Mock ProductDetailCategory existingDetailCategory,
                              @Mock ProductDetailCategory sameNameDetailCategory) throws Exception {
                // given
                request.setName("테스트 이름");
                request.setNewRootCategoryId(2L);
                rootCategoryId = 1L;
                detailCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);

                doReturn(true)
                        .when(existingDetailCategory).isKindOf(rootCategoryId);
                doReturn(false)
                        .when(existingDetailCategory).isSame(request.getName());
                doReturn(existingDetailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);

                doReturn(false)
                        .when(sameNameDetailCategory).isSame(detailCategoryId);
                doReturn(true)
                        .when(sameNameDetailCategory).isKindOf(rootCategoryId);
                doReturn(sameNameDetailCategory)
                        .when(adminCategoryMapper).getDetailCategoryByName(request.getName());

                // when & then
                assertThrows(ConflictException.class, () -> {
                    service.updateDetailCategory(rootCategoryId, detailCategoryId, request);
                });
            }
        }
    }

    @Nested
    class deleteRootCategory {
        Long rootCategoryId;

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            void test() throws Exception {
                // given
                rootCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
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
        @DisplayName("실패 케이스")
        class Fail {

            @Test
            @DisplayName("미존재")
            void notFound() {
                // given
                rootCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(null).when(adminCategoryMapper).getRootCategory(rootCategoryId);

                // when & then
                assertThrows(NotFoundException.class, () -> {
                    service.deleteRootCategory(rootCategoryId);
                });
            }

            @Test
            @DisplayName("사용하는 상세 카테고리가 존재")
            void detailUsingRootExist() {
                // given
                rootCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                List<ProductDetailCategory> detailCategories = new ArrayList<>();
                detailCategories.add(new ProductDetailCategory());

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(detailCategories)
                        .when(adminCategoryMapper).getDetailCategoriesIncludedInRootCategory(rootCategoryId);

                // when & then
                assertThrows(PreconditionFailedException.class, () -> {
                    service.deleteRootCategory(rootCategoryId);
                });
            }
        }
    }

    @Nested
    class deleteDetailCategory {
        Long rootCategoryId, detailCategoryId;

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            void test(@Mock ProductDetailCategory detailCategory) throws Exception {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(true)
                        .when(detailCategory).isKindOf(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);
                doReturn(new ArrayList<Product>())
                        .when(adminCategoryMapper).getProductsIncludedInDetailCategory(detailCategoryId);

                // when
                SuccessfulResponse response = service.deleteDetailCategory(rootCategoryId, detailCategoryId);

                // then
                assertTrue(response.getSuccess());
            }
        }


        @Nested
        @DisplayName("실패 케이스")
        class Fail {

            @Test
            @DisplayName("최상위 카테고리 미존재")
            void rootCategoryNotExist() {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(null)
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);

                // when & then
                assertThrows(NotFoundException.class, () -> {
                    service.deleteDetailCategory(rootCategoryId, detailCategoryId);
                });
            }

            @Test
            @DisplayName("상세 카테고리 미존재")
            void detailCategoryNotExist() {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(null)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);

                // when & then
                assertThrows(NotFoundException.class, () -> {
                    service.deleteDetailCategory(rootCategoryId, detailCategoryId);
                });
            }

            @Test
            @DisplayName("최상위 카테고리에 포함되지 않음")
            void notIncludedInRoot(@Mock ProductDetailCategory detailCategory) {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(false)
                        .when(detailCategory).isKindOf(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);

                // when & then
                assertThrows(PreconditionFailedException.class, () -> {
                    service.deleteDetailCategory(rootCategoryId, detailCategoryId);
                });
            }

            @Test
            @DisplayName("사용하는 상품이 존재")
            void productUsingDetailExist(@Mock ProductDetailCategory detailCategory) {
                // given
                rootCategoryId = 1L;
                detailCategoryId = 1L;

                service = new AdminCategoryServiceImpl(adminCategoryMapper);

                doReturn(new ProductRootCategory())
                        .when(adminCategoryMapper).getRootCategory(rootCategoryId);
                doReturn(true)
                        .when(detailCategory).isKindOf(rootCategoryId);
                doReturn(detailCategory)
                        .when(adminCategoryMapper).getDetailCategory(detailCategoryId);

                List<Product> products = new ArrayList<>();
                products.add(new Product());

                doReturn(products)
                        .when(adminCategoryMapper).getProductsIncludedInDetailCategory(detailCategoryId);

                // when & then
                assertThrows(PreconditionFailedException.class, () -> {
                    service.deleteDetailCategory(rootCategoryId, detailCategoryId);
                });
            }
        }
    }
}