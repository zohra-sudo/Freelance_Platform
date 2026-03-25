<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="jakarta.faces.html"
      xmlns:f="jakarta.faces.core">

<h:head>
    <title>Freelance - Connexion</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,700;1,700&amp;family=Inter:wght@400;500;600&amp;display=swap" rel="stylesheet"/>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }

        :root {
            --primary:       #C47D2B;
            --primary-light: #F5E6D0;
            --primary-hover: #a8691e;
            --dark:          #1a1a1a;
            --text:          #2d2d2d;
            --text-muted:    #888;
            --border:        #e8e0d5;
            --bg:            #f7f4ef;
            --white:         #ffffff;
            --radius:        14px;
            --error:         #e53e3e;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: var(--bg);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        /* ── NAVBAR ── */
        nav {
            display: flex; align-items: center; justify-content: space-between;
            padding: 18px 60px; background: var(--white);
            border-bottom: 1px solid var(--border);
        }
        .nav-logo {
            font-family: 'Playfair Display', serif; font-style: italic;
            font-size: 1.6rem; color: var(--primary); text-decoration: none;
        }

        /* ── MAIN LAYOUT ── */
        .auth-wrapper {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 60px 20px;
        }

        .auth-container {
            display: flex;
            width: 100%;
            max-width: 960px;
            background: var(--white);
            border-radius: 24px;
            overflow: hidden;
            box-shadow: 0 20px 60px rgba(0,0,0,0.08);
        }

        /* ── LEFT PANEL (décoratif) ── */
        .auth-left {
            flex: 1;
            background: linear-gradient(160deg, #1e1a12 0%, #2d2010 50%, #3d2e18 100%);
            padding: 60px 50px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            position: relative;
            overflow: hidden;
        }
        .auth-left::before {
            content: '';
            position: absolute;
            top: -80px; right: -80px;
            width: 300px; height: 300px;
            background: radial-gradient(circle, rgba(196,125,43,0.3) 0%, transparent 70%);
            border-radius: 50%;
        }
        .auth-left::after {
            content: '';
            position: absolute;
            bottom: -60px; left: -60px;
            width: 250px; height: 250px;
            background: radial-gradient(circle, rgba(196,125,43,0.2) 0%, transparent 70%);
            border-radius: 50%;
        }
        .auth-left-top { position: relative; z-index: 1; }
        .auth-left-badge {
            display: inline-block;
            background: rgba(196,125,43,0.2);
            color: var(--primary);
            padding: 6px 14px; border-radius: 50px;
            font-size: 0.75rem; font-weight: 600;
            letter-spacing: 0.08em; text-transform: uppercase;
            margin-bottom: 28px;
        }
        .auth-left h2 {
            font-family: 'Playfair Display', serif; font-style: italic;
            font-size: 2.2rem; color: #fff; line-height: 1.3; margin-bottom: 16px;
        }
        .auth-left p { color: #aaa; font-size: 0.95rem; line-height: 1.7; }

        .auth-features { position: relative; z-index: 1; }
        .auth-feature {
            display: flex; align-items: center; gap: 14px;
            margin-bottom: 18px;
        }
        .auth-feature-icon {
            width: 40px; height: 40px;
            background: rgba(196,125,43,0.15);
            border: 1px solid rgba(196,125,43,0.3);
            border-radius: 10px;
            display: flex; align-items: center; justify-content: center;
            flex-shrink: 0;
        }
        .auth-feature span {
            color: #ccc; font-size: 0.88rem; line-height: 1.5;
        }
        .auth-feature strong { color: #fff; display: block; font-size: 0.9rem; margin-bottom: 2px; }

        /* ── RIGHT PANEL (formulaire) ── */
        .auth-right {
            flex: 1;
            padding: 60px 50px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        /* ── TABS ── */
        .auth-tabs {
            display: flex;
            background: var(--bg);
            border-radius: 50px;
            padding: 4px;
            margin-bottom: 36px;
        }
        .auth-tab {
            flex: 1; text-align: center;
            padding: 10px; border-radius: 50px;
            font-size: 0.9rem; font-weight: 600;
            cursor: pointer; transition: all 0.25s;
            color: var(--text-muted); background: transparent; border: none;
            text-decoration: none; display: block;
        }
        .auth-tab.active {
            background: var(--white);
            color: var(--primary);
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        /* ── FORM ── */
        .auth-title {
            font-family: 'Playfair Display', serif; font-style: italic;
            font-size: 1.8rem; color: var(--dark); margin-bottom: 6px;
        }
        .auth-subtitle { color: var(--text-muted); font-size: 0.9rem; margin-bottom: 32px; }

        .form-row { display: flex; gap: 16px; }
        .form-group { margin-bottom: 20px; flex: 1; }
        .form-group label {
            display: block; font-size: 0.85rem; font-weight: 600;
            color: var(--text); margin-bottom: 8px;
        }
        .form-group input,
        .form-group select {
            width: 100%; padding: 12px 16px;
            border: 1.5px solid var(--border);
            border-radius: 10px; font-size: 0.9rem;
            font-family: 'Inter', sans-serif;
            color: var(--text); background: var(--white);
            transition: border-color 0.2s, box-shadow 0.2s;
            outline: none;
        }
        .form-group input:focus,
        .form-group select:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(196,125,43,0.1);
        }
        .form-group input::placeholder { color: #bbb; }

        .form-footer {
            display: flex; align-items: center; justify-content: space-between;
            margin-bottom: 24px;
        }
        .remember { display: flex; align-items: center; gap: 8px; font-size: 0.85rem; color: var(--text-muted); }
        .remember input[type="checkbox"] { accent-color: var(--primary); }
        .forgot { font-size: 0.85rem; color: var(--primary); text-decoration: none; font-weight: 500; }
        .forgot:hover { text-decoration: underline; }

        .btn-submit {
            width: 100%;
            background: var(--primary); color: #fff;
            border: none; padding: 14px;
            border-radius: 50px; font-size: 1rem;
            font-weight: 600; cursor: pointer;
            transition: background 0.2s, transform 0.1s;
            font-family: 'Inter', sans-serif;
        }
        .btn-submit:hover { background: var(--primary-hover); transform: translateY(-1px); }
        .btn-submit:active { transform: translateY(0); }

        .divider {
            display: flex; align-items: center; gap: 12px;
            margin: 24px 0; color: var(--text-muted); font-size: 0.8rem;
        }
        .divider::before, .divider::after {
            content: ''; flex: 1; height: 1px; background: var(--border);
        }

        .switch-text {
            text-align: center; font-size: 0.88rem; color: var(--text-muted); margin-top: 20px;
        }
        .switch-text a { color: var(--primary); font-weight: 600; text-decoration: none; }
        .switch-text a:hover { text-decoration: underline; }

        .error-msg {
            background: #fff5f5; border: 1px solid #fed7d7;
            color: var(--error); padding: 12px 16px;
            border-radius: 10px; font-size: 0.85rem;
            margin-bottom: 20px; display: none;
        }
        .error-msg.show { display: block; }

        /* ── TOGGLE LOGIN/REGISTER ── */
        .panel { display: none; }
        .panel.active { display: block; }
    </style>
</h:head>

<h:body>

    <!-- NAVBAR -->
    <nav>
        <a href="index.xhtml" class="nav-logo">Freelance</a>
    </nav>

    <!-- MAIN -->
    <div class="auth-wrapper">
        <div class="auth-container">

            <!-- ── LEFT PANEL ── -->
            <div class="auth-left">
                <div class="auth-left-top">
                    <span class="auth-left-badge">Freelance Platform</span>
                    <h2>Connect with the best talent</h2>
                    <p>Join thousands of professionals and clients building great things together.</p>
                </div>
                <div class="auth-features">
                    <div class="auth-feature">
                        <div class="auth-feature-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#C47D2B" stroke-width="2">
                                <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
                                <circle cx="9" cy="7" r="4"/>
                                <path d="M23 21v-2a4 4 0 00-3-3.87"/>
                                <path d="M16 3.13a4 4 0 010 7.75"/>
                            </svg>
                        </div>
                        <div>
                            <strong>10,000+ Freelancers</strong>
                            <span>Verified professionals in every domain</span>
                        </div>
                    </div>
                    <div class="auth-feature">
                        <div class="auth-feature-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#C47D2B" stroke-width="2">
                                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                            </svg>
                        </div>
                        <div>
                            <strong>Secure Payments</strong>
                            <span>Funds held safely until work is approved</span>
                        </div>
                    </div>
                    <div class="auth-feature">
                        <div class="auth-feature-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#C47D2B" stroke-width="2">
                                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                            </svg>
                        </div>
                        <div>
                            <strong>Quality Guarantee</strong>
                            <span>Every project meets the highest standards</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ── RIGHT PANEL ── -->
            <div class="auth-right">

                <!-- TABS -->
                <div class="auth-tabs">
                    <a href="#" class="auth-tab active" id="tab-login" onclick="showPanel('login'); return false;">
                        Se connecter
                    </a>
                    <a href="#" class="auth-tab" id="tab-register" onclick="showPanel('register'); return false;">
                        S'inscrire
                    </a>
                </div>

                <!-- ══ PANEL LOGIN ══ -->
                <div class="panel active" id="panel-login">
                    <div class="auth-title">Bon retour !</div>
                    <div class="auth-subtitle">Connectez-vous pour accéder à votre espace</div>

                    <h:form id="loginForm">
                        <!-- Message erreur -->
                        <h:panelGroup rendered="#{not empty authBean.messageErreur}">
                            <div class="error-msg show">
                                <h:outputText value="#{authBean.messageErreur}"/>
                            </div>
                        </h:panelGroup>

                        <div class="form-group">
                            <label>Adresse email</label>
                            <h:inputText value="#{authBean.email}"
                                         styleClass="input-field"
                                         style="width:100%; padding:12px 16px; border:1.5px solid #e8e0d5; border-radius:10px; font-size:0.9rem; outline:none; font-family:'Inter',sans-serif;"
                                         placeholder="votre@email.com"
                                         required="true"/>
                        </div>

                        <div class="form-group">
                            <label>Mot de passe</label>
                            <h:inputSecret value="#{authBean.motDePasse}"
                                           style="width:100%; padding:12px 16px; border:1.5px solid #e8e0d5; border-radius:10px; font-size:0.9rem; outline:none; font-family:'Inter',sans-serif;"
                                           placeholder="••••••••"
                                           required="true"/>
                        </div>

                        <div class="form-footer">
                            <label class="remember">
                                <input type="checkbox"/> Se souvenir de moi
                            </label>
                            <a href="#" class="forgot">Mot de passe oublié ?</a>
                        </div>

                        <h:commandButton value="Se connecter"
                                         action="#{authBean.connecter}"
                                         styleClass="btn-submit"/>
                    </h:form>

                    <div class="switch-text">
                        Pas encore inscrit ?
                        <a href="#" onclick="showPanel('register'); return false;">Créer un compte</a>
                    </div>
                </div>

                <!-- ══ PANEL INSCRIPTION ══ -->
                <div class="panel" id="panel-register">
                    <div class="auth-title">Créer un compte</div>
                    <div class="auth-subtitle">Rejoignez notre communauté de freelances et clients</div>

                    <h:form id="registerForm">
                        <h:panelGroup rendered="#{not empty authBean.messageErreur}">
                            <div class="error-msg show">
                                <h:outputText value="#{authBean.messageErreur}"/>
                            </div>
                        </h:panelGroup>

                        <div class="form-row">
                            <div class="form-group">
                                <label>Nom complet</label>
                                <h:inputText value="#{authBean.nouveauUser.nom}"
                                             style="width:100%; padding:12px 16px; border:1.5px solid #e8e0d5; border-radius:10px; font-size:0.9rem; outline:none; font-family:'Inter',sans-serif;"
                                             placeholder="Votre nom"
                                             required="true"/>
                            </div>
                            <div class="form-group">
                                <label>Domaine</label>
                                <h:selectOneMenu value="#{authBean.nouveauUser.domaine}"
                                                 style="width:100%; padding:12px 16px; border:1.5px solid #e8e0d5; border-radius:10px; font-size:0.9rem; outline:none; font-family:'Inter',sans-serif;">
                                    <f:selectItem itemValue=""          itemLabel="Choisir..."/>
                                    <f:selectItem itemValue="Web Development"   itemLabel="Web Development"/>
                                    <f:selectItem itemValue="Graphic Design"    itemLabel="Graphic Design"/>
                                    <f:selectItem itemValue="Video Editing"     itemLabel="Video Editing"/>
                                    <f:selectItem itemValue="Content Writing"   itemLabel="Content Writing"/>
                                    <f:selectItem itemValue="UI/UX Design"      itemLabel="UI/UX Design"/>
                                    <f:selectItem itemValue="Digital Marketing" itemLabel="Digital Marketing"/>
                                </h:selectOneMenu>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Adresse email</label>
                            <h:inputText value="#{authBean.nouveauUser.email}"
                                         style="width:100%; padding:12px 16px; border:1.5px solid #e8e0d5; border-radius:10px; font-size:0.9rem; outline:none; font-family:'Inter',sans-serif;"
                                         placeholder="votre@email.com"
                                         required="true"/>
                        </div>

                        <div class="form-group">
                            <label>Mot de passe</label>
                            <h:inputSecret value="#{authBean.nouveauUser.motDePasse}"
                                           style="width:100%; padding:12px 16px; border:1.5px solid #e8e0d5; border-radius:10px; font-size:0.9rem; outline:none; font-family:'Inter',sans-serif;"
                                           placeholder="Minimum 8 caractères"
                                           required="true"/>
                        </div>

                        <div class="form-group">
                            <label>Bio (optionnel)</label>
                            <h:inputText value="#{authBean.nouveauUser.bio}"
                                         style="width:100%; padding:12px 16px; border:1.5px solid #e8e0d5; border-radius:10px; font-size:0.9rem; outline:none; font-family:'Inter',sans-serif;"
                                         placeholder="Décrivez-vous en quelques mots..."/>
                        </div>

                        <h:commandButton value="Créer mon compte"
                                         action="#{authBean.inscrire}"
                                         styleClass="btn-submit"/>
                    </h:form>

                    <div class="switch-text">
                        Déjà inscrit ?
                        <a href="#" onclick="showPanel('login'); return false;">Se connecter</a>
                    </div>
                </div>

            </div>
        </div>
    </div>

    <script>
        function showPanel(panel) {
            // Panels
            document.getElementById('panel-login').classList.remove('active');
            document.getElementById('panel-register').classList.remove('active');
            document.getElementById('panel-' + panel).classList.add('active');
            // Tabs
            document.getElementById('tab-login').classList.remove('active');
            document.getElementById('tab-register').classList.remove('active');
            document.getElementById('tab-' + panel).classList.add('active');
        }
    </script>

</h:body>
</html>
