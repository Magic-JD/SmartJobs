INSERT INTO public.credential (username,"password") VALUES
	 ('email@email.com','{noop}password1');
INSERT INTO public.cv (file_hash,condensed_text) VALUES
	 ('HASH','The best in the world'),
	 ('HASH2','The worst in the world');
INSERT INTO public."role" (user_id,"position") VALUES
	 (1,'role');
INSERT INTO public.selected_role (user_id,role_id) VALUES
	 (1,1);
INSERT INTO public.candidate ("name",cv_id,last_accessed,user_id,currently_selected) VALUES
	 ('Joe',1,'2024-06-26',1,true),
	 ('James',2,'2024-06-26',1,false);
INSERT INTO public.candidate_role(id, role_id, candidate_id) VALUES
    (1, 1, 1),
    (1, 1, 2);
INSERT INTO public.defined_criteria (id,category,criteria,"input",ai_prompt,is_boolean,input_example,tooltip) VALUES
	 (2,'Soft Skills','Communication and Teamwork',false,'Rate communication and teamwork skills on a CV from 0 to 10: 0 for no evidence; 1-3 for basic collaboration roles; 4-6 for regular involvement in team projects; 7-9 for leadership in team settings; 10 for exceptional, documented achievements in enhancing team performance and communication.',false,NULL,'0 : No evidence of collaboration roles
1-3 : Basic involvement in collaborative projects or teams
4-6 : Regular participation and contribution in team projects
7-9 : Leadership demonstrated within team settings
10 : Exceptional, documented achievements in enhancing team performance and communication'),
	 (10,'Qualifications','Degree',true,'Score qualifications on a CV for a degree in subject ''X'' as follows: 10 for possessing a degree in ''X''; 5 for a degree in a related field; 2 for no degree in ''X'' but other forms of education in the subject; 0 for no form of education in ''X''.',false,'Computer Science','0 for no experience; 1-3 for up to 2 years; 4-6 for 3-5 years; 7-9 for 6-9 years; 10 for 10 or more years of in-depth, continuous experience.'),
	 (6,'Hard Skills','Language Skills',true,'Evaluate ''X'' language skills on a CV from 0 to 10: 1-3 for some evidence of language skills, 4-6 for experience in X-speaking environments, 7-10 for advanced qualifications and use of X in a business environment',false,'German','0: No evidence of language skills.
1-3: Some evidence of language skills, indicating basic understanding or limited usage.
4-6: Experience in environments where this language is primarily spoken, suggesting proficiency acquired through immersion or prolonged exposure.
7-10: Advanced qualifications and use of the language in a business environment, demonstrating high proficiency, possibly including specialized knowledge or certification.'),
	 (11,'Qualifications','Postgraduate Degree',true,'Score qualifications on a CV for advanced degrees in subject ''X'' as follows: 10 for a PhD in ''X'' or a closely related field; 5 for a Master''s degree in ''X'' or a closely related field. ',false,'Philosophy','10 for a PhD in ''X'' or a closely related field; 5 for a Master''s degree in ''X'' or a closely related field. '),
	 (16,'Qualifications','Special Certification or Licence',true,'Score qualifications on a CV for special certification or license ''X'' as follows: true if the candidate possesses the certification or license ''X''; otherwise, false.',true,'Chartered Financial Analyst (CFA)','Full marks if the candidate possesses the certification or license ''X''; otherwise, 0 marks.'),
	 (1,'Soft Skills','Management Experience',false,'Assess management experience on a CV on a scale from 0 to 10: 0 indicates no experience; 1-3 for leading small teams or projects; 4-6 for managing departments; 7-9 for overseeing multiple departments; 10 for executive leadership across an entire organization.',false,NULL,'0 : no management experience
1-3 : The candidate has lead small teams or projects
4-6 : The candidate has managed departments
7-9 : The candidate has overseen multiple departments
10 : The candidate has held a position of executive leadership across an entire organization'),
	 (4,'Hard Skills','Software Systems',true,'Evaluate skills with ''X'' on a CV from 0 to 10: 0 for no familiarity; 1-3 for basic use, 4-6 for competent use, 7-9 for advanced functionality and/or administrator responsibility, 10 for expert-level mastery and innovation.',false,'Microsoft Excel','0: No familiarity with the software.
1-3: Basic use of the software, indicating fundamental understanding and usage.
4-6: Competent use of the software, demonstrating proficiency and efficiency in its application.
7-9: Advanced functionality and/or administrator responsibility, showcasing adeptness in utilizing complex features or managing system-level tasks.
10: Expert-level mastery of the software, marked by innovative solutions, deep understanding of its intricacies, and significant contributions to its enhancement or development.'),
	 (9,'Relevant Experience','Years of Experience',true,'Measure years of experience in discipline ''X'' on a CV from 0 to 10: 0 for no experience; 1-3 for up to 2 years; 4-6 for 3-5 years; 7-9 for 6-9 years; 10 for 10 or more years of in-depth, continuous experience.',false,'Baking','0 for no experience; 1-3 for up to 2 years; 4-6 for 3-5 years; 7-9 for 6-9 years; 10 for 10 or more years of in-depth, continuous experience.'),
	 (19,'Hard Skills','Numeracy and Statistics',false,'Evaluate numerical skills on a CV from 0 to 10: 1-3 for basic evidence of tracking budgets and metrics, 4-6 for intermediate skills including data analysis and financial management, 7-10 for advanced proficiency in complex analysis demonstrated through achievements or certifications.',false,NULL,'0: No evidence of applied numeracal ability in a role.
1-3: Basic evidence of tracking budgets and metrics, indicating a foundational understanding or minimal experience.
4-6: Intermediate skills, including data analysis and financial management, suggesting a deeper understanding and practical application in managing budgets and metrics.
7-10: Advanced proficiency in complex analysis demonstrated through achievements or certifications, showcasing mastery in analyzing financial data, making strategic decisions, and potentially holding certifications or recognized achievements in the field.'),
	 (7,'Relevant Experience','Professional Engagement and Recognition',true,'Rate company-type relevance to the industry ''X'' on a CV from 0 to 10: 0 indicates no connection; 1-3 for related sectors or skills; 4-6 for indirect roles within the industry; 7-9 for direct roles and deep industry knowledge; 10 for extensive, specialized experience.',false,'Print Media','0: No connection to the industry or company type.
1-3: Related sectors or skills, suggesting some peripheral knowledge or experience.
4-6: Indirect roles within the industry or company type, indicating experience or exposure to industry practices but not directly involved.
7-9: Direct roles and deep knowledge, demonstrating significant experience and understanding of industry operations.
10: Extensive, specialized experience in the industry or company type, showcasing mastery and possibly leadership in specific domains or sectors within the industry.');
INSERT INTO public.defined_criteria (id,category,criteria,"input",ai_prompt,is_boolean,input_example,tooltip) VALUES
	 (13,'Professional Engagement and Recognition','Membership in Professional Organizations',false,'Score a CV for membership of professional organizations. Return true if found, false otherwise.',true,NULL,'Full marks if found, 0 otherwise.'),
	 (14,'Professional Engagement and Recognition','Volunteer Work',false,'Score a CV for evidence of volunteer work. Return true if found, false otherwise.',true,NULL,'Full marks if found, 0 otherwise.'),
	 (15,'Professional Engagement and Recognition','Awards and Honors',false,'Score a CV for evidence of awards and honours. Return true if found, false otherwise.',true,NULL,'Full marks if found, 0 otherwise.'),
	 (17,'Hard Skills','Programming Language',true,'Evaluate proficiency in programming language ''X'' on a CV from 0-10 based on job roles, project complexity, years of experience, and certifications. Assign levels as follows: Beginner (0-3) handles straightforward tasks; Intermediate (4-6) manages moderate projects and some complex issues; Expert (7-10) leads advanced projects, solves complex problems, and demonstrates thought leadership.',false,'Java','0: No familiarity with the technology.
1-3: Basic use of the technology in question, with limited proficiency.
4-6: Regular and competent application of the technology, demonstrating proficiency in its use.
7-9: Advanced utilization of the technology, with adept problem-solving skills and the ability to handle complex tasks.
10: Expert-level mastery of the technology, including innovative approaches and contributions to its advancement.'),
	 (18,'Hard Skills','Cloud Computing Technologies',true,'Assess expertise in cloud software technology ''X'' on a scale of 0-10. Consider years of experience, certifications, project complexity, and leadership roles. Define levels: Beginner (0-3) for basic operations, Intermediate (4-6) for solution deployment, and Expert (7-10) for designing architecture and leading strategy.',false,'Kubernetes','0: No familiarity with the cloud technology.
1-3: Basic operations in the cloud technology, indicating fundamental understanding and usage.
4-6: Competent in deploying solutions using the cloud technology, demonstrating proficiency and efficiency.
7-9: Advanced in designing architecture and managing strategy, showcasing adeptness in complex functionalities.
10: Expert-level mastery in the cloud technology, marked by innovative solutions, deep understanding of its intricacies, and significant leadership contributions.'),
	 (3,'Soft Skills','Customer Service Skills',false,'Assess direct customer-facing skills on a CV from 0 to 10. Do not consider indirect forms of customer service: 0 for no exposure; 1-3 for occasional customer interaction; 4-6 for regular customer service roles; 7-9 for extensive customer management and conflict resolution; 10 for expert-level customer engagement and proven customer satisfaction leadership',false,NULL,'0: No exposure to customer interaction.
1-3: Occasional customer interaction, perhaps in a supportive role or sporadically within tasks.
4-6: Regular involvement in customer service roles, handling inquiries, and basic support.
7-9: Extensive experience in customer management, adept at resolving conflicts and managing complex interactions.
10: Expert-level engagement with customers, demonstrated leadership in ensuring high levels of customer satisfaction and loyalty'),
	 (8,'Relevant Experience','Relevant Role Experience',true,'Evaluate role relevance to role: ''X'' on a CV from 0 to 10: 0 for unrelated experience; 1-3 for foundational skills applicable to the role; 4-6 for moderate experience in similar roles; 7-9 for extensive direct experience; 10 for expert-level proficiency and leadership in role X.',false,'Marketing Director','0 for unrelated experience; 1-3 for foundational skills applicable to the role; 4-6 for moderate experience in similar roles; 7-9 for extensive direct experience; 10 for expert-level proficiency and leadership in role X.'),
	 (12,'Professional Engagement and Recognition','Professional Development and Engagement',false,'Score Professional Development and Engagement on a CV: Score 1-3 for basic involvement, 4-6 for active participation, 7-9 for leadership roles or significant contributions, and 10 for exemplary impact or recognized authority in the field.',false,'','1-3 for basic involvement, 4-6 for active participation, 7-9 for leadership roles or significant contributions, and 10 for exemplary impact or recognized authority in the field.');

