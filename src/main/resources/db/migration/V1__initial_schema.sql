--
-- PostgreSQL database dump
--

-- Dumped from database version 14.17 (Homebrew)
-- Dumped by pg_dump version 14.17 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: equipment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.equipment (
    id character varying(255) NOT NULL,
    createdate timestamp(6) without time zone,
    gym_id character varying(255),
    imageicon text,
    name character varying(255),
    updatedate timestamp(6) without time zone,
    user_id character varying(255),
    category character varying(255),
    description text
);


--
-- Name: equipment_exercises; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.equipment_exercises (
    equipment_id character varying(255) NOT NULL,
    exercise_id character varying(255) NOT NULL
);


--
-- Name: exercise_muscles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.exercise_muscles (
    exercise_id character varying(255) NOT NULL,
    muscle_name character varying(255)
);


--
-- Name: exercises; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.exercises (
    id character varying(255) NOT NULL,
    instructions text,
    name character varying(255)
);


--
-- Name: user_equipment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_equipment (
    equipment_id character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    uuid character varying(255) NOT NULL,
    createdat timestamp(6) without time zone,
    deviceid character varying(255),
    email character varying(255),
    name character varying(255),
    token character varying(255),
    updatedat timestamp(6) without time zone,
    password character varying(255)
);


--
-- Name: workout_exercises; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workout_exercises (
    workout_id character varying(255) NOT NULL,
    exercise_id character varying(255) NOT NULL
);


--
-- Name: workout_sessions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.workout_sessions (
    id character varying(255) NOT NULL,
    completeddate timestamp(6) without time zone,
    iscompleted boolean NOT NULL,
    scheduleddate timestamp(6) without time zone,
    user_id character varying(255)
);


--
-- Name: equipment_exercises equipment_exercises_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.equipment_exercises
    ADD CONSTRAINT equipment_exercises_pkey PRIMARY KEY (equipment_id, exercise_id);


--
-- Name: equipment equipment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.equipment
    ADD CONSTRAINT equipment_pkey PRIMARY KEY (id);


--
-- Name: exercises exercises_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exercises
    ADD CONSTRAINT exercises_pkey PRIMARY KEY (id);


--
-- Name: users uk3g1j96g94xpk3lpxl2qbl985x; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk3g1j96g94xpk3lpxl2qbl985x UNIQUE (name);


--
-- Name: user_equipment user_equipment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_equipment
    ADD CONSTRAINT user_equipment_pkey PRIMARY KEY (equipment_id, user_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (uuid);


--
-- Name: workout_exercises workout_exercises_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workout_exercises
    ADD CONSTRAINT workout_exercises_pkey PRIMARY KEY (workout_id, exercise_id);


--
-- Name: workout_sessions workout_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workout_sessions
    ADD CONSTRAINT workout_sessions_pkey PRIMARY KEY (id);


--
-- Name: workout_exercises fk3bn8puhdk29j682es5643p60c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workout_exercises
    ADD CONSTRAINT fk3bn8puhdk29j682es5643p60c FOREIGN KEY (exercise_id) REFERENCES public.exercises(id);


--
-- Name: exercise_muscles fk6b8j6uvfbioes1e0r7ctcahcq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exercise_muscles
    ADD CONSTRAINT fk6b8j6uvfbioes1e0r7ctcahcq FOREIGN KEY (exercise_id) REFERENCES public.exercises(id);


--
-- Name: workout_exercises fk9xskr6pmpaay349corpgp699d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workout_exercises
    ADD CONSTRAINT fk9xskr6pmpaay349corpgp699d FOREIGN KEY (workout_id) REFERENCES public.workout_sessions(id);


--
-- Name: equipment_exercises fkapmytqca104kkenekpo20vubt; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.equipment_exercises
    ADD CONSTRAINT fkapmytqca104kkenekpo20vubt FOREIGN KEY (equipment_id) REFERENCES public.equipment(id);


--
-- Name: equipment fkb5l8jn49f28013dm206lgybpx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.equipment
    ADD CONSTRAINT fkb5l8jn49f28013dm206lgybpx FOREIGN KEY (user_id) REFERENCES public.users(uuid);


--
-- Name: equipment_exercises fkf4884ki2sl18exmyhhcl6t8i0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.equipment_exercises
    ADD CONSTRAINT fkf4884ki2sl18exmyhhcl6t8i0 FOREIGN KEY (exercise_id) REFERENCES public.exercises(id);


--
-- Name: workout_sessions fkfwqciawyjntpphp080wpa37ge; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.workout_sessions
    ADD CONSTRAINT fkfwqciawyjntpphp080wpa37ge FOREIGN KEY (user_id) REFERENCES public.users(uuid);


--
-- Name: user_equipment fkl4sdl24xxdordflh1ikuydr02; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_equipment
    ADD CONSTRAINT fkl4sdl24xxdordflh1ikuydr02 FOREIGN KEY (equipment_id) REFERENCES public.equipment(id);


--
-- Name: user_equipment fknaer8lbpfkvn4jacwovlij79h; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_equipment
    ADD CONSTRAINT fknaer8lbpfkvn4jacwovlij79h FOREIGN KEY (user_id) REFERENCES public.users(uuid);


--
-- PostgreSQL database dump complete
--

