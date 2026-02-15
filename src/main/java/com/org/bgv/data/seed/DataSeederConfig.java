package com.org.bgv.data.seed;

import com.org.bgv.common.RoleConstants;
import com.org.bgv.common.navigation.CreateNavigationMenuDto;
import com.org.bgv.common.navigation.NavigationResponseDto;
import com.org.bgv.entity.BGVCategory;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.CheckType;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.CompanyUser;
import com.org.bgv.entity.DegreeDocumentType;
import com.org.bgv.entity.DegreeType;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.FieldOfStudy;
import com.org.bgv.entity.Other;
import com.org.bgv.entity.Permission;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.RolePermission;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.repository.BGVCategoryRepository;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.CheckTypeRepository;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.DegreeDocumentTypeRepository;
import com.org.bgv.repository.DegreeTypeRepository;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.FieldOfStudyRepository;
import com.org.bgv.repository.NavigationMenuRepository;
import com.org.bgv.repository.OtherRepository;
import com.org.bgv.repository.PermissionRepository;
import com.org.bgv.repository.RolePermissionRepository;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;
import com.org.bgv.service.EmailService;
import com.org.bgv.service.NavigationMenuService;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataSeederConfig implements CommandLineRunner {

	private final CheckCategoryRepository categoryRepo;
	private final DocumentTypeRepository docTypeRepo;
	private final PermissionRepository permissionRepository;
	private final RolePermissionRepository rolePermissionRepository;
	private final DegreeTypeRepository degreeTypeRepository;
	private final FieldOfStudyRepository fieldOfStudyRepository;
	private final OtherRepository otherRepository;
	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final BGVCategoryRepository bgvCategoryRepository;
	private final CheckTypeRepository checkTypeJPARepository;
	private final CompanyRepository companyRepository;
	private final CompanyUserRepository companyUserRepository;
	private final NavigationMenuService navigationMenuService;
	private final NavigationMenuRepository navigationMenuRepository;
	private final EmailService emailTemplateService;
	private final DegreeDocumentTypeRepository degreeDocumentTypeRepository;

	@Override
	// @EventListener(ApplicationReadyEvent.class)
	public void run(String... args) {
		// seedDocumentCategoriesAndTypes();
		seedPermissions();
		seedRoles();
		seedRolePermissions();
		seedDegreeTypes();
		seedFieldsOfStudy();
		seedSingleOtherRecord();
		seedDefaultAdminUser();
		seedDegreeDocumentMapping();

		// seedBGVCategoriesAndCheckTypes();
		// setdefaultnavigationSeed();
		// emailTemplateService.initializeTemplatesFromFiles();

	}

	private void seedDefaultAdminUser() {
		if (userRepository.findByEmail("admin@example.com").isEmpty()) {
			User adminUser = User.builder()
					// .firstName("System Administrator")
					.email("admin@example.com").password(passwordEncoder.encode("123456"))
					// .firstName("System")
					// .lastName("Administrator")
					// .phoneNumber("+1234567890")
					.userType("ADMIN").build();

			User savedAdmin = userRepository.save(adminUser);

			// Assign ROLE_ADMIN to the admin user
			Role adminRole = roleRepository.findByName("Administrator")
					.orElseThrow(() -> new RuntimeException("Administrator not found"));

			UserRole userRole = UserRole.builder().user(savedAdmin).role(adminRole).build();
			userRoleRepository.save(userRole);
			Boolean isExisted = companyRepository.existsByCompanyName("default");
			if (!isExisted) {
				Company defaultCompany = createDefaultCompany();
				Company savedCompany = companyRepository.save(defaultCompany);
				CompanyUser companyUser = createCompanyUser(savedCompany, savedAdmin);
				companyUserRepository.save(companyUser);
			}

			System.out.println("Default admin user created successfully");
		}
	}

	// Alternative method: Create a single sample record if none exists
	private void seedSingleOtherRecord() {
		if (otherRepository.count() == 0) {
			Other sampleRecord = Other.builder().build();
			otherRepository.save(sampleRecord);
			System.out.println("Sample Other record created successfully");
		}
	}

	private void seedDocumentCategoriesAndTypes() {
		// Prepare category objects with labels
		List<CheckCategory> categories = Arrays.asList(createCategory("IDENTITY_PROOF", "Identity Proof", "IDENTITY"),
				createCategory("EDUCATION", "Education", "EDUCATION"),
				createCategory("WORK_EXPERIENCE", "Professional/Work Experience", "WORK"),
				createCategory("OTHER", "Other", "OTHER"), createCategory("ADDRESS", "Address", "ADDRESS"),
				createCategory("COURT", "Court", "COURT"));

		// Save categories and prepare document types
		for (CheckCategory category : categories) {
			CheckCategory savedCategory = saveCategoryIfNotExists(category);
			seedDocumentTypesForCategory(savedCategory);
		}
	}

	private CheckCategory createCategory(String name, String label, String code) {
		return CheckCategory.builder().name(name).label(label).code(code).build();
	}

	private CheckCategory saveCategoryIfNotExists(CheckCategory category) {
		Optional<CheckCategory> existingCategory = categoryRepo.findByName(category.getName());
		return existingCategory.orElseGet(() -> categoryRepo.save(category));
	}

	private void seedDocumentTypesForCategory(CheckCategory category) {
		List<DocumentType> documentTypes = getDocumentTypesForCategory(category);

		for (DocumentType documentType : documentTypes) {
			saveDocumentTypeIfNotExists(documentType, category);
		}
	}

	private List<DocumentType> getDocumentTypesForCategory(CheckCategory category) {
		switch (category.getName().toUpperCase()) {
		case "IDENTITY_PROOF":
			return Arrays.asList(createDocumentType("AADHAR", "Aadhar Card", category, "AADHAAR"),
					createDocumentType("PANCARD", "PAN Card", category, "PAN"),
					createDocumentType("PASSPORT", "Passport", category, "PASSPORT"),
					createDocumentType("VOTER_ID", "Voter ID", category, "VOTER"),
					createDocumentType("DRIVING_LICENCE", "Driving Licence", category, "DL"));
		case "EDUCATION":
			return Arrays.asList(createDocumentType("SSC_MARKSHEET", "SSC Marksheet", category, "SSC_MARKSHEET"),
					createDocumentType("HSC_MARKSHEET", "HSC Marksheet", category, "HSC_MARKSHEET"),
					createDocumentType("GRADUATION_CERTIFICATE", "Graduation Certificate", category,
							"GRADUATION_CERTIFICATE"),
					createDocumentType("POST_GRADUATION_CERTIFICATE", "Post Graduation Certificate", category,
							"POST_GRADUATION_CERTIFICATE"));
		case "WORK_EXPERIENCE":
			return Arrays.asList(
					createDocumentType("EXPERIENCE_LETTER", "Experience Letter", category, "EXPERIENCE_LETTER"),
					createDocumentType("OFFER_LETTER", "Offer Letter", category, "OFFER_LETTER"),
					createDocumentType("RELIEVING_LETTER", "Relieving Letter", category, "RELIEVING_LETTER"),
					createDocumentType("PAYSLIP", "Payslip", category, "PAYSLIP"),
					createDocumentType("APPOINTMENT_LETTER", "Appointment Letter", category, "APPOINTMENT_LETTER"));
		case "ADDRESS":
			return Arrays.asList(createDocumentType("ADDR_PERM", "Permanent Address", category, "ADDR_PERM"),
					createDocumentType("ADDR_CURR", "Current Address", category, "ADDR_CURR"));

		case "OTHER":
			return Arrays.asList(createDocumentType("OTHER", "Other Document", category, "OTHER"));
		default:
			return Arrays.asList();
		}
	}

	private DocumentType createDocumentType(String name, String label, CheckCategory category, String code) {
		return DocumentType.builder().name(name).label(label).category(category).code(code).build();
	}

	private void saveDocumentTypeIfNotExists(DocumentType documentType, CheckCategory category) {
		if (docTypeRepo.findByNameAndCategory_CategoryId(documentType.getName(), category.getCategoryId()).isEmpty()) {
			docTypeRepo.save(documentType);
		}
	}

	private void seedPermissions() {
		// Fixed permission names to match what's used in rolePermissions mapping
		List<Permission> defaultPermissions = Arrays.asList(createPermission("CREATE_PROFILE", "Create Profile"),
				createPermission("EDIT_PROFILE", "Edit Profile"), createPermission("DELETE_PROFILE", "Delete Profile"),
				createPermission("VIEW_PROFILE", "View Profile"), createPermission("MANAGE_USERS", "Manage Users"), // Changed
																													// from
																													// MANAGE_PROFILES
				createPermission("VIEW_PROFILES", "View Profiles"));

		for (Permission permission : defaultPermissions) {
			savePermissionIfNotExists(permission);
		}
	}

	private Permission createPermission(String name, String label) {
		return Permission.builder().name(name).label(label).build();
	}

	private void savePermissionIfNotExists(Permission permission) {
		permissionRepository.findByName(permission.getName()).orElseGet(() -> permissionRepository.save(permission));
	}

	private void seedRoles() {
		List<Role> defaultRoles = Arrays.asList(createRole("User", "User", RoleConstants.TYPE_REGULAR),
				createRole("Administrator", "Administrator", RoleConstants.TYPE_REGULAR),

				createRole("Vendor User", "Vendor User", RoleConstants.TYPE_VENDOR),
				createRole("Vendor Administrator", "Vendor Administrator", RoleConstants.TYPE_VENDOR),
				createRole("Vendor Verifier", "Vendor Verifier", RoleConstants.TYPE_VENDOR),

				createRole("Candidate", "Candidate", RoleConstants.TYPE_COMPANY),
				createRole("Company Administrator", "Company Administrator", RoleConstants.TYPE_COMPANY),
				createRole("Company HR Manager", "Company HR Manager", RoleConstants.TYPE_COMPANY),
				createRole("Recruiter", "Recruiter", RoleConstants.TYPE_COMPANY),
				createRole("Company", "Company", RoleConstants.TYPE_COMPANY)

		);

		for (Role role : defaultRoles) {
			saveRoleIfNotExists(role);
		}
	}

	private Role createRole(String name, String label, Long type) {
		return Role.builder().name(name).label(label).type(type).build();
	}

	private void saveRoleIfNotExists(Role role) {
		roleRepository.findByName(role.getName()).orElseGet(() -> roleRepository.save(role));
	}

	private void seedRolePermissions() {
		// Fixed permission names to match what we seeded
		Map<String, List<String>> rolePermissions = Map.of("ROLE_USER",
				Arrays.asList("VIEW_PROFILES", "CREATE_PROFILE", "EDIT_PROFILE", "VIEW_PROFILE"), "ROLE_VENDOR",
				Arrays.asList("VIEW_PROFILES", "VIEW_PROFILE"), "ROLE_ADMIN", Arrays.asList("MANAGE_USERS",
						"VIEW_PROFILES", "CREATE_PROFILE", "EDIT_PROFILE", "DELETE_PROFILE", "VIEW_PROFILE"));

		for (Map.Entry<String, List<String>> entry : rolePermissions.entrySet()) {
			String roleName = entry.getKey();
			Role role = roleRepository.findByName(roleName).orElse(null);
			if (role == null)
				continue;

			for (String permName : entry.getValue()) {
				Permission permission = permissionRepository.findByName(permName).orElse(null);
				if (permission == null) {
					System.out.println("Permission not found: " + permName);
					continue;
				}

				saveRolePermissionIfNotExists(role, permission);
			}
		}
	}

	private void saveRolePermissionIfNotExists(Role role, Permission permission) {
		boolean exists = rolePermissionRepository.existsByRoleAndPermission(role, permission);
		if (!exists) {
			RolePermission rp = RolePermission.builder().role(role).permission(permission).build();
			rolePermissionRepository.save(rp);
		}
	}

	private void seedDegreeTypes() {
		List<DegreeType> defaultDegrees = Arrays.asList(createDegreeType("SSC", "Secondary School Certificate"),
				createDegreeType("HSC", "Higher Secondary Certificate"), createDegreeType("DIPLOMA", "Diploma"),
				createDegreeType("BTECH", "Bachelor of Technology"), createDegreeType("BE", "Bachelor of Engineering"),
				createDegreeType("BSC", "Bachelor of Science"),
				createDegreeType("BCA", "Bachelor of Computer Applications"),
				createDegreeType("MBA", "Master of Business Administration"),
				createDegreeType("MTECH", "Master of Technology"), createDegreeType("MSC", "Master of Science"),
				createDegreeType("MCA", "Master of Computer Applications"),
				createDegreeType("PHD", "Doctor of Philosophy"), createDegreeType("OTHER", "Other Degree"));

		for (DegreeType degree : defaultDegrees) {
			saveDegreeTypeIfNotExists(degree);
		}
	}

	private DegreeType createDegreeType(String name, String label) {
		return DegreeType.builder().name(name).label(label).build();
	}

	private void saveDegreeTypeIfNotExists(DegreeType degreeType) {
		degreeTypeRepository.findByName(degreeType.getName()).orElseGet(() -> degreeTypeRepository.save(degreeType));
	}

	private void seedFieldsOfStudy() {
		List<FieldOfStudy> fields = Arrays.asList(createFieldOfStudy("Computer Science", "Computer Science"),
				createFieldOfStudy("Information Technology", "Information Technology"),
				createFieldOfStudy("Electronics", "Electronics"),
				createFieldOfStudy("Electrical", "Electrical Engineering"),
				createFieldOfStudy("Mechanical", "Mechanical Engineering"),
				createFieldOfStudy("Civil", "Civil Engineering"), createFieldOfStudy("Biotechnology", "Biotechnology"),
				createFieldOfStudy("Finance", "Finance"), createFieldOfStudy("Marketing", "Marketing"),
				createFieldOfStudy("Human Resources", "Human Resources"),
				createFieldOfStudy("Data Science", "Data Science"),
				createFieldOfStudy("Artificial Intelligence", "Artificial Intelligence"),
				createFieldOfStudy("Other", "Other Field"));

		for (FieldOfStudy field : fields) {
			saveFieldOfStudyIfNotExists(field);
		}
	}

	private FieldOfStudy createFieldOfStudy(String name, String label) {
		return FieldOfStudy.builder().name(name).label(label).build();
	}

	private void saveFieldOfStudyIfNotExists(FieldOfStudy field) {
		fieldOfStudyRepository.findByName(field.getName()).orElseGet(() -> fieldOfStudyRepository.save(field));
	}

	/*
	 * private void seedBGVCategoriesAndCheckTypes() { // Step 1: Seed categories
	 * List<BGVCategory> categories = Arrays.asList( createBGVCategory("IDENTITY",
	 * "Identity Verification"), createBGVCategory("EDUCATION",
	 * "Education Verification"), createBGVCategory("EMPLOYMENT",
	 * "Employment Verification"), createBGVCategory("ADDRESS",
	 * "Address Verification"), createBGVCategory("CRIMINAL",
	 * "Criminal Record Check"), createBGVCategory("FINANCIAL",
	 * "Financial Verification"), createBGVCategory("REFERENCE", "Reference Check"),
	 * createBGVCategory("HEALTH", "Health Check"), createBGVCategory("SOCIAL",
	 * "Social Media Screening"), createBGVCategory("OTHER", "Other Checks") );
	 * 
	 * for (BGVCategory category : categories) {
	 * bgvCategoryRepository.findByName(category.getName()) .orElseGet(() ->
	 * bgvCategoryRepository.save(category)); }
	 * 
	 * // Step 2: Seed check types seedBGVCheckTypes(); }
	 */
	/*
	 * private BGVCategory createBGVCategory(String name, String label) { return
	 * BGVCategory.builder() .name(name) .label(label) .isActive(Boolean.TRUE)
	 * .build(); }
	 * 
	 * private void seedBGVCheckTypes() { Map<String, List<String[]>>
	 * categoryToCheckTypes = Map.of( "IDENTITY", Arrays.asList( new
	 * String[]{"AADHAAR_VERIFICATION", "Aadhaar Verification"}, new
	 * String[]{"PAN_VERIFICATION", "PAN Card Verification"}, new
	 * String[]{"PASSPORT_VERIFICATION", "Passport Verification"}, new
	 * String[]{"VOTER_ID_VERIFICATION", "Voter ID Verification"} ), "EDUCATION",
	 * Arrays.asList( new String[]{"DEGREE_VERIFICATION", "Degree Verification"},
	 * new String[]{"MARKSHEET_VERIFICATION", "Marksheet Verification"} ),
	 * "EMPLOYMENT", Arrays.asList( new String[]{"PREVIOUS_EMPLOYMENT_VERIFICATION",
	 * "Previous Employment Verification"}, new String[]{"TENURE_VERIFICATION",
	 * "Tenure Verification"}, new String[]{"SALARY_VERIFICATION",
	 * "Salary Verification"} ), "ADDRESS", Arrays.asList( new
	 * String[]{"CURRENT_ADDRESS_VERIFICATION", "Current Address Verification"}, new
	 * String[]{"PERMANENT_ADDRESS_VERIFICATION", "Permanent Address Verification"}
	 * ), "CRIMINAL", Arrays.asList( new String[]{"POLICE_VERIFICATION",
	 * "Police Verification"}, new String[]{"COURT_RECORD_CHECK",
	 * "Court Record Check"}, new String[]{"GLOBAL_WATCHLIST_CHECK",
	 * "Global Watchlist Check"} ), "FINANCIAL", Arrays.asList( new
	 * String[]{"CREDIT_CHECK", "Credit History Check"}, new String[]{"CIBIL_CHECK",
	 * "CIBIL Score Check"} ), "REFERENCE", Arrays.asList( new
	 * String[]{"PROFESSIONAL_REFERENCE_CHECK", "Professional Reference Check"}, new
	 * String[]{"CHARACTER_REFERENCE_CHECK", "Character Reference Check"} ),
	 * "HEALTH", Arrays.asList( new String[]{"DRUG_TEST", "Drug Test"}, new
	 * String[]{"MEDICAL_FITNESS", "Medical Fitness Check"} ), "SOCIAL",
	 * Arrays.asList( new String[]{"SOCIAL_MEDIA_SCREENING",
	 * "Social Media Screening"}, new String[]{"ONLINE_REPUTATION_CHECK",
	 * "Online Reputation Check"} ), "OTHER", Arrays.asList( new
	 * String[]{"GAP_ANALYSIS", "Gap Analysis"}, new
	 * String[]{"PROFESSIONAL_LICENSE_VERIFICATION",
	 * "Professional License Verification"} ) );
	 * 
	 * categoryToCheckTypes.forEach((categoryName, checkTypes) -> { BGVCategory
	 * category = bgvCategoryRepository.findByName(categoryName) .orElseThrow(() ->
	 * new RuntimeException("Category not found: " + categoryName));
	 * 
	 * for (String[] ct : checkTypes) { String name = ct[0]; String label = ct[1];
	 * 
	 * checkTypeJPARepository.findByName(name) .orElseGet(() ->
	 * checkTypeJPARepository.save( CheckType.builder() .name(name) .label(label)
	 * .category(category) .build() )); } });
	 * 
	 * System.out.println("✅ BGV Categories and Check Types seeded successfully"); }
	 */

	private Company createDefaultCompany() {
		Company company = new Company();
		company.setCompanyName("default");
		// company.setCompanyType("default");
		// company.setRegistrationNumber("BGV-ADMIN-001");
		// company.setTaxId("TAX-ADMIN-001");
		// company.setIncorporationDate(LocalDate.now());
		// company.setIndustry("");
		// company.setCompanySize("1-10");
		company.setWebsite("https://bgventures.com");
		company.setDescription("Default administration company ");

		// Contact Information
		company.setContactPersonName("System Administrator");
		company.setContactPersonTitle("Administrator");
		company.setContactEmail("admin@example.com");
		company.setContactPhone("");
		/*
		 * // Address Information company.setAddressLine1("123 Administration Street");
		 * company.setCity("Tech City"); company.setState("California");
		 * company.setCountry("United States"); company.setZipCode("90001");
		 * company.setStatus("ACTIVE");
		 * 
		 * // Additional Information
		 * company.setLinkedinProfile("https://linkedin.com/company/bgventures");
		 */
		return company;
	}

	private CompanyUser createCompanyUser(Company company, User user) {
		CompanyUser companyUser = new CompanyUser();
		companyUser.setCompany(company);
		companyUser.setUser(user);
		companyUser.setCompanyId(company.getId());
		companyUser.setUserId(user.getUserId());
		return companyUser;
	}

	private void setdefaultnavigationSeed() {

		List<String> permissions = new ArrayList<>();
		permissions.add("Administrator");

		if (navigationMenuRepository.existsByNameAndParentId("Settings", null)) {
			// throw new IllegalArgumentException("Navigation menu with name already
			// exists");
		} else {
			CreateNavigationMenuDto createNavigationMenuDto = createDefaultNavigation("Settings", "Settings",
					"/admin/settings/", "Settings", "Section", permissions, true, null, 0L);
			NavigationResponseDto createdMenu = navigationMenuService.createNavigationMenu(createNavigationMenuDto);

			navigationMenuService.createNavigationMenu(createDefaultNavigation("Create Page", "Create Page",
					"/admin/settings/pages/new", "FileText", "Link", permissions, true, createdMenu.getId(), 0L));
			navigationMenuService.createNavigationMenu(createDefaultNavigation("Email Templates", "Email Templates",
					"/admin/settings/email-templates", "Mail", "Link", permissions, true, createdMenu.getId(), 0L));
		}
	}

	private CreateNavigationMenuDto createDefaultNavigation(String name, String label, String href, String icon,
			String type, List<String> permissions, Boolean isActive, Long parentId, Long menuOrder) {
		return CreateNavigationMenuDto.builder().name(name)
				// .type(type)
				.label(label).href(href).icon(icon).permissions(permissions).parentId(parentId).isActive(isActive)
				.order(0).build();

	}

	private void seedDegreeDocumentMapping() {

		DegreeType ssc = degreeTypeRepository.findByName("SSC").orElse(null);
		DegreeType hsc = degreeTypeRepository.findByName("HSC").orElse(null);
		DegreeType btech = degreeTypeRepository.findByName("BTECH").orElse(null);
		DegreeType mba = degreeTypeRepository.findByName("MBA").orElse(null);

		DocumentType sscMarksheet = docTypeRepo.findByName("SSC_MARKSHEET").orElse(null);

		DocumentType hscMarksheet = docTypeRepo.findByName("HSC_MARKSHEET").orElse(null);

		DocumentType gradCert = docTypeRepo.findByName("GRADUATION_CERTIFICATE").orElse(null);

		DocumentType pgCert = docTypeRepo.findByName("POST_GRADUATION_CERTIFICATE").orElse(null);

		// SSC
		saveDegreeDocMapping(ssc, sscMarksheet, true);

		// HSC
		saveDegreeDocMapping(hsc, hscMarksheet, true);

		// BTECH
		saveDegreeDocMapping(btech, gradCert, true);

		// MBA
		saveDegreeDocMapping(mba, pgCert, true);
	}

	private void saveDegreeDocMapping(DegreeType degree, DocumentType doc, boolean required) {

		if (degree == null || doc == null)
			return;

		boolean exists = degreeDocumentTypeRepository.findAll().stream()
				.anyMatch(m -> m.getDegreeType().getDegreeId().equals(degree.getDegreeId())
						&& m.getDocumentType().getDocTypeId().equals(doc.getDocTypeId()));

		if (!exists) {
			DegreeDocumentType mapping = DegreeDocumentType.builder().degreeType(degree).documentType(doc)
					.required(required).active(true).build();

			degreeDocumentTypeRepository.save(mapping);
		}
	}

}